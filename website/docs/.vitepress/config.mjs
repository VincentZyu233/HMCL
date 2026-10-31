import { defineConfig } from 'vitepress'

export default defineConfig({
  title: 'HMCL 增强版',
  description: '支持自建 OpenBMCLAPI 节点动态加速与双轨故障转移 | 陆大四楼工作站节点',
  base: '/HMCL/',
  head: [
    ['link', { rel: 'icon', href: '/HMCL/custom_hmcl_logo.png' }],
    ['link', { rel: 'stylesheet', href: 'https://cdn.jsdelivr.net/npm/lxgw-wenkai-screen-webfont@1.1.0/style.css' }]
  ],
  themeConfig: {
    logo: '/custom_hmcl_logo.png',
    nav: [
      { text: '首页', link: '/' },
      { text: '接入指南', link: '/guide' },
      { text: '实时看板', link: 'https://laoke.vincentzyu233.cn:14000/dashboard/' },
      { text: 'GitHub 仓库', link: 'https://github.com/VincentZyu233/HMCL' }
    ],
    sidebar: [
      {
        text: '使用指引',
        items: [
          { text: '项目介绍', link: '/' },
          { text: '节点接入教程', link: '/guide' }
        ]
      }
    ],
    socialLinks: [
      { icon: 'github', link: 'https://github.com/VincentZyu233/HMCL' }
    ],
    footer: {
      message: '基于 GPL v3 开源协议发布 | 陆大四楼工作站节点',
      copyright: 'Copyright © 2026 VincentZyu'
    }
  }
})
