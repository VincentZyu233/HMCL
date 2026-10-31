// SPDX-FileCopyrightText: Copyright (C) 2026 The HMCL Authors
// SPDX-License-Identifier: GPL-3.0-or-later WITH AdditionRef-HMCL
package org.jackhuang.hmcl.download;

import org.glavo.url.WebURL;
import org.jetbrains.annotations.NotNullByDefault;
import org.jetbrains.annotations.Nullable;

import javax.net.ssl.*;
import java.net.HttpURLConnection;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.Base64;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.jackhuang.hmcl.util.logging.Logger.LOG;

/// Utility class for rewriting OpenBMCLAPI download redirects to a custom cluster node
/// with dual-mode failover (official cluster domain vs fallback/penetration URL)
/// and scoped lenient SSL support.
@NotNullByDefault
public final class OpenBMCLAPICustomNode {

    private OpenBMCLAPICustomNode() {
    }

    private static volatile boolean enabled = false;
    private static volatile String clusterId = "";
    private static volatile String officialPort = "443";
    private static volatile String nodeUrl = "";
    private static volatile String clusterSecret = "";
    private static volatile boolean insecureSsl = true;

    // Cache active resolved endpoint for 10 minutes (600,000 ms)
    private static volatile String cachedActiveEndpoint = "";
    private static final AtomicLong cacheExpireTime = new AtomicLong(0);

    private static final Pattern OPENBMCLAPI_DOWNLOAD_PATTERN =
            Pattern.compile("^/download/([0-9a-fA-F]{40})(?:\\?.*)?$");

    @Nullable
    private static volatile SSLSocketFactory lenientSSLSocketFactory = null;
    private static final HostnameVerifier LENIENT_HOSTNAME_VERIFIER = (hostname, session) -> true;

    public static boolean isEnabled() {
        return enabled;
    }

    public static void setEnabled(boolean value) {
        enabled = value;
        invalidateCache();
    }

    public static String getClusterId() {
        return clusterId;
    }

    public static void setClusterId(@Nullable String id) {
        clusterId = id == null ? "" : id.trim();
        invalidateCache();
    }

    public static String getOfficialPort() {
        return officialPort;
    }

    public static void setOfficialPort(@Nullable String port) {
        officialPort = (port == null || port.isBlank()) ? "443" : port.trim();
        invalidateCache();
    }

    public static String getNodeUrl() {
        return nodeUrl;
    }

    public static void setNodeUrl(@Nullable String url) {
        nodeUrl = url == null ? "" : url.trim();
        invalidateCache();
    }

    public static String getClusterSecret() {
        return clusterSecret;
    }

    public static void setClusterSecret(@Nullable String secret) {
        clusterSecret = secret == null ? "" : secret.trim();
        invalidateCache();
    }

    public static boolean isInsecureSsl() {
        return insecureSsl;
    }

    public static void setInsecureSsl(boolean value) {
        insecureSsl = value;
    }

    public static void invalidateCache() {
        cachedActiveEndpoint = "";
        cacheExpireTime.set(0);
    }

    /// Computes the official cluster node URL if clusterId is configured.
    public static String buildOfficialNodeUrl() {
        String id = clusterId;
        if (id.isEmpty()) {
            return "";
        }
        String port = officialPort;
        if ("443".equals(port)) {
            return "https://" + id + ".openbmclapi.933.moe";
        } else {
            return "https://" + id + ".openbmclapi.933.moe:" + port;
        }
    }

    /// Checks if a given host matches the configured custom node addresses.
    public static boolean isCustomNodeHost(@Nullable String host) {
        if (!enabled || host == null) {
            return false;
        }
        String id = clusterId;
        if (!id.isEmpty()) {
            String officialHost = id + ".openbmclapi.933.moe";
            if (host.equalsIgnoreCase(officialHost)) {
                return true;
            }
        }
        String fallback = nodeUrl;
        if (!fallback.isEmpty()) {
            try {
                WebURL fallbackUrl = WebURL.parseBrowserInput(fallback);
                String fallbackHost = fallbackUrl.getHost();
                if (fallbackHost != null && host.equalsIgnoreCase(fallbackHost)) {
                    return true;
                }
            } catch (Exception ignored) {
            }
        }
        return false;
    }

    /// Configures an HTTP connection with scoped lenient SSL if it targets our custom node.
    public static void applyCustomSSLIfNeeded(URLConnection connection, WebURL url) {
        if (!enabled || !insecureSsl) {
            return;
        }
        if (!(connection instanceof HttpsURLConnection httpsURLConnection)) {
            return;
        }
        String host = url.getHost();
        if (isCustomNodeHost(host)) {
            httpsURLConnection.setHostnameVerifier(LENIENT_HOSTNAME_VERIFIER);
            SSLSocketFactory factory = getOrCreateLenientSSLSocketFactory();
            if (factory != null) {
                httpsURLConnection.setSSLSocketFactory(factory);
            }
        }
    }

    @Nullable
    private static SSLSocketFactory getOrCreateLenientSSLSocketFactory() {
        if (lenientSSLSocketFactory == null) {
            synchronized (OpenBMCLAPICustomNode.class) {
                if (lenientSSLSocketFactory == null) {
                    try {
                        SSLContext sslContext = SSLContext.getInstance("TLS");
                        TrustManager[] trustAllCerts = new TrustManager[]{
                                new X509TrustManager() {
                                    @Override
                                    public void checkClientTrusted(X509Certificate[] chain, String authType) {
                                    }

                                    @Override
                                    public void checkServerTrusted(X509Certificate[] chain, String authType) {
                                    }

                                    @Override
                                    public X509Certificate[] getAcceptedIssuers() {
                                        return new X509Certificate[0];
                                    }
                                }
                        };
                        sslContext.init(null, trustAllCerts, new SecureRandom());
                        lenientSSLSocketFactory = sslContext.getSocketFactory();
                    } catch (GeneralSecurityException e) {
                        LOG.warning("Failed to initialize lenient SSL context for custom OpenBMCLAPI node", e);
                    }
                }
            }
        }
        return lenientSSLSocketFactory;
    }

    /// Resolves the currently active endpoint base URL (either official or fallback).
    public static String getActiveNodeUrl() {
        long now = System.currentTimeMillis();
        String cached = cachedActiveEndpoint;
        if (!cached.isEmpty() && now < cacheExpireTime.get()) {
            return cached;
        }

        String official = buildOfficialNodeUrl();
        String fallback = nodeUrl;

        // If only one is configured, use it directly
        if (!official.isEmpty() && fallback.isEmpty()) {
            cachedActiveEndpoint = official;
            cacheExpireTime.set(now + 600_000L);
            return official;
        }
        if (official.isEmpty() && !fallback.isEmpty()) {
            cachedActiveEndpoint = fallback;
            cacheExpireTime.set(now + 600_000L);
            return fallback;
        }
        if (official.isEmpty() && fallback.isEmpty()) {
            return "";
        }

        // Both are configured: test official first with a quick probe
        boolean officialAlive = probeEndpoint(official);
        if (officialAlive) {
            cachedActiveEndpoint = official;
            cacheExpireTime.set(now + 600_000L);
            return official;
        } else {
            cachedActiveEndpoint = fallback;
            cacheExpireTime.set(now + 600_000L);
            return fallback;
        }
    }

    private static boolean probeEndpoint(String rawUrl) {
        try {
            WebURL url = WebURL.parseBrowserInput(rawUrl);
            URLConnection conn = url.toURL().openConnection();
            if (conn instanceof HttpURLConnection httpConn) {
                httpConn.setConnectTimeout(800);
                httpConn.setReadTimeout(800);
                httpConn.setRequestMethod("GET");
                httpConn.setInstanceFollowRedirects(false);
                applyCustomSSLIfNeeded(httpConn, url);
                httpConn.connect();
                int code = httpConn.getResponseCode();
                httpConn.disconnect();
                return code > 0;
            }
        } catch (Throwable e) {
            LOG.info("Custom OpenBMCLAPI official endpoint probe failed: " + rawUrl + " (" + e.getMessage() + ")");
        }
        return false;
    }

    /// Rewrites a 302 redirection target URL if it matches an OpenBMCLAPI file download endpoint.
    public static WebURL rewriteUrl(WebURL originalTarget) {
        if (!enabled) {
            return originalTarget;
        }

        String currentSecret = clusterSecret;
        if (currentSecret.isEmpty()) {
            return originalTarget;
        }

        String currentNodeUrl = getActiveNodeUrl();
        if (currentNodeUrl.isEmpty()) {
            return originalTarget;
        }

        String host = originalTarget.getHost();
        if (host == null) {
            return originalTarget;
        }
        String hostLower = host.toLowerCase(Locale.ROOT);
        if (!hostLower.contains("openbmclapi") && !hostLower.contains("bangbang93.com")) {
            return originalTarget;
        }

        String href = originalTarget.href();
        int queryIdx = href.indexOf('?');
        String path = (queryIdx >= 0) ? href.substring(0, queryIdx) : href;

        int slashIdx = path.indexOf("://");
        if (slashIdx >= 0) {
            int pathStart = path.indexOf('/', slashIdx + 3);
            if (pathStart >= 0) {
                path = path.substring(pathStart);
            } else {
                path = "/";
            }
        }

        Matcher matcher = OPENBMCLAPI_DOWNLOAD_PATTERN.matcher(path);
        if (!matcher.matches()) {
            return originalTarget;
        }

        String hash = matcher.group(1).toLowerCase(Locale.ROOT);
        long exp = System.currentTimeMillis() + 300_000L;

        String sign = sign(hash, currentSecret, exp);
        if (sign == null) {
            return originalTarget;
        }

        String base = currentNodeUrl.endsWith("/")
                ? currentNodeUrl.substring(0, currentNodeUrl.length() - 1)
                : currentNodeUrl;
        String newUrlStr = base + "/download/" + hash + "?s=" + sign + "&e=" + exp;

        try {
            return WebURL.parse(newUrlStr);
        } catch (Exception e) {
            LOG.warning("Failed to parse custom OpenBMCLAPI node URL: " + newUrlStr, e);
            return originalTarget;
        }
    }

    @Nullable
    private static String sign(String hash, String secret, long exp) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            String data = secret + hash + exp;
            byte[] hashBytes = digest.digest(data.getBytes(StandardCharsets.UTF_8));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(hashBytes);
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
    }
}
