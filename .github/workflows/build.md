# HMCL Fork CI & Release Guide

本仓库集成了全自动的 GitHub Actions CI/CD 流水线（`.github/workflows/ci.yml`），由 Commit 消息标签或手动 Dispatch 驱动。

---

## 🏷️ Commit 标签驱动说明

| Commit 标签 | 阶段 1：Checkstyle 规范检查 | 阶段 2：全平台编译 (Artifacts) | 阶段 3：发布 GitHub Release |
| :--- | :---: | :---: | :---: |
| *(无标签常规提交)* | ✅ 永远执行 (~30s) | ❌ 跳过 | ❌ 跳过 |
| **`[build-action]`** | ✅ 永远执行 | ✅ 交叉编译 4 大产物 | ❌ 跳过 |
| **`[build-release]`** | ✅ 永远执行 | ✅ 交叉编译 4 大产物 | ✅ 自动发布 GitHub Release |

---

## 🚀 使用示例

### 1. 仅日常提交（快速代码规范检查，不浪费构建时长）
```bash
git commit -m "fix: adjust layout parameters"
git push
```

### 2. 触发编译并生成可下载产物（供试用测试）
```bash
git commit -m "feat: enhance cache logic [build-action]"
git push
```
完成后在 GitHub 仓库的 **Actions -> CI -> Summary** 页面下方直接下载生成的 `HMCL-*-artifacts`。

### 3. 一键编译并自动发布 Release
```bash
git commit -m "release: v3.17.0-vincentzyu.1 [build-release]"
git push
```
GitHub Actions 会自动编译 Windows `.exe`、Linux `.deb`、Linux `.sh`、跨平台 `.jar`，自动计算 SHA-256 校验和表格，并自动在 GitHub Releases 发布。

### 4. 网页端手动触发
进入 GitHub 仓库页面 -> **Actions** -> **CI** -> **Run workflow** -> 选择执行模式（`check-only` / `build` / `release`）。
