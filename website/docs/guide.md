# 节点接入使用指南

本页面详细介绍如何将 **VincentZyu Fork 增强版客户端** 或 **官方上游原版 HMCL** 接入我们的陆大四楼 OpenBMCLAPI 私有分发节点。

---

## 姿势一：使用 VincentZyu Fork 增强版（最推荐）

> **适用场景**：想要开箱即用图形化界面、全自动故障自愈与毫秒级探活的玩家。

### 1. 下载客户端
从项目 Release 页面下载已完成编译的最新发布版：
👉 **[下载 HMCL-3.17.0-vincentzyu.1.exe](https://github.com/VincentZyu233/HMCL/releases/tag/v3.17.0-vincentzyu.1)**

### 2. 配置自建节点加速
1. 启动 `HMCL-3.17.0-vincentzyu.1.exe`；
2. 进入 **「启动器设置」 ➔ 「下载设置」**；
3. 勾选 **`[✔] 启用自建 OpenBMCLAPI 节点加速`**；
4. 填入加 QQ 获取到的专属参数：
   - **Cluster ID**：`6adf0d69576aa033c38b0759`（选填，优先尝试官方动态解析）
   - **服务器地址 / 穿透 IP**：`https://laoke.vincentzyu233.cn:14000`
   - **Cluster Secret**：加 QQ 获取的私有鉴权密钥
   - **`[✔] 忽略 SSL 证书与域名校验`**（保持默认勾选）
5. 点击 **「测试连通性」** 按钮，弹出绿色连通提示即可放心使用！

---

## 姿势二：使用官方上游原版 HMCL（零修改）

> **适用场景**：只信任官方发布版、不想更换任何已有客户端的老玩家。

借助我们部署在 AMD 机器上的签名透明网关，官方原版无需修改任何代码，只需追加一个 JVM 参数即可全量分流！

### 1. 通过命令行参数启动
为你的原版 `HMCL-3.17.0.363.exe` 创建桌面快捷方式，在「属性 ➔ 目标」末尾追加参数：
```cmd
"HMCL-3.17.0.363.exe" -Dhmcl.bmclapi.override=http://laoke.vincentzyu233.cn:14001
```

### 2. 使用一键 BAT 脚本启动
在原版 HMCL 所在文件夹中新建 `启动HMCL_开启陆大加速.bat`，内容如下：
```bat
@echo off
start "" "HMCL-3.17.0.363.exe" -Dhmcl.bmclapi.override=http://laoke.vincentzyu233.cn:14001 %*
```
双击 BAT 脚本启动，你的所有版本下载、资源补丁与依赖库都将 100% 自动由陆大四楼 iMac 节点高速分发！

---

## 实时监控与公开观景台

所有经由我们节点分发的请求，都会实时记录在公开大盘中：
👉 **[访问 aplPanel 实时监控看板](https://laoke.vincentzyu233.cn:14000/dashboard/)**

- 实时带宽曲线与当日总流量；
- 近期用户代理（HMCL / PCL2 等版本统计）；
- 节点运行健康度与分发命中率。

---

## 节点授权与获取配置

<div class="author-box">
  <img src="https://q1.qlogo.cn/g?b=qq&nk=1830540513&s=640" alt="VincentZyu" class="author-avatar" />
  <div>
    <h3 style="margin: 0; font-size: 1.15rem; font-weight: 600;">加 QQ 索取专属配置</h3>
    <p style="margin: 6px 0 0 0; color: var(--vp-c-text-2);">
      QQ：<code>1830540513</code> ｜ 维护者：VincentZyu
    </p>
    <p style="margin: 6px 0 0 0; font-size: 0.9rem;">
      添加好友请备注：<strong>【MC加速】</strong>。验证通过后即可秒发节点配置与启动脚本！
    </p>
  </div>
</div>
