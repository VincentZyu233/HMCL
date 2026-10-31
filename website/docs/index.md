---
layout: home

hero:
  name: "HMCL 增强版"
  text: "自建 OpenBMCLAPI 节点动态加速与双轨故障转移"
  tagline: "基于陆大四楼工作站的高性能 Minecraft 资源分发网络"
  image:
    src: /custom_hmcl_logo.png
    alt: HMCL VincentZyu
  actions:
    - theme: brand
      text: 🚀 获取增强版客户端
      link: https://github.com/VincentZyu233/HMCL/releases/tag/v3.17.0-vincentzyu.1
    - theme: alt
      text: 📖 节点接入教程
      link: /guide
    - theme: alt
      text: 📊 实时监控看板
      link: https://laoke.vincentzyu233.cn:14000/dashboard/

features:
  - icon: ⚡
    title: 双轨探活与秒级回退
    details: 支持官方动态集群域名与专用穿透 IP 双地址并发健康探测，超时自动降级，保障 100% 下载成功率。
  - icon: 🛡️
    title: 宽容 SSL 域名校验
    details: 独立隔离的自签名与穿透别名证书放行机制，彻底告别 HTTPS 证书域名不匹配与握手报错。
  - icon: 🧩
    title: 兼容上游官方原版
    details: 官方原版 HMCL 无需重新编译，仅需一条启动参数即可透明享受陆大四楼节点加速。
  - icon: 📈
    title: 透明公开的实时大盘
    details: 部署专属 aplPanel 看板，随时随地公开透明查阅节点实时带宽、请求量与分发状态。
---

<div class="author-box">
  <img src="https://q1.qlogo.cn/g?b=qq&nk=1830540513&s=640" alt="VincentZyu" class="author-avatar" />
  <div>
    <h3 style="margin: 0; font-size: 1.25rem; font-weight: 600;">开发者 / 节点维护者：VincentZyu</h3>
    <p style="margin: 6px 0 0 0; color: var(--vp-c-text-2); font-size: 0.95rem;">
      QQ：<code>1830540513</code> ｜ 节点位置：陆大四楼 Linux 工作站 (iMac 2015 硬件)
    </p>
    <p style="margin: 6px 0 0 0; font-size: 0.9rem; color: var(--vp-c-brand-1);">
      💡 为了防止公共带宽被爬虫和未知流量滥用，节点密钥与专用参数不对外明文公开。如需使用本节点加速，欢迎加我 QQ 备注【MC加速】索取专属配置！
    </p>
  </div>
</div>
