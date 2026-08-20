# HopeCraft-NG - Bukkit核心功能增强插件
[![GitHub](https://img.shields.io/badge/GitHub-源码-blue?logo=github)](https://github.com/busysheng/hopecraft-ng)  
[![License](https://img.shields.io/badge/License-MPL--2.0-orange)](https://www.mozilla.org/en-US/MPL/2.0/)

专为 **Bukkit 1.21+** 设计的轻量级工具集。HopeCraft-NG 是原 [HopeCraft](https://github.com/BusyMitten/HopeCraft)（1.7.0，已停止维护 EOL）的继承版本，合并了 1.7.0 与 NG 双方的新功能。

## ✨ 与 1.7.0 相比的变化
- 主包名由 `org.hopestudio.hopeCraft` 变更为 `org.hopestudio`
- **移除**主菜单中的 Shift+F 切换按钮（第 22 格绿色/灰色染料按钮）；Shift+F 快捷键功能本身保留（可通过 `/hopecraft shiftf <on|off|reload>` 控制）
- **移除**签到/统计的刷屏日志输出（签到完成、统计数据保存等高频操作不再向控制台打印日志）
- 新增管理员账号自动 OP（`AUTO_OP_NAMES`）
- 保留 1.7.0 新增的实用命令 `/suicide`、`/heal`、`/feed`

## 🚀 核心功能
实现菜单功能，代码简洁易读写，拓展空间大。
效果图
![效果](https://cn-sy1.rains3.com/hope/2026/02/6585.jpg)

- 主菜单：传送 / 每日签到 / 我的统计 / 烟花 / 获取头颅 / 生日信息 / 修改MOTD
- 每日签到奖励由 `config.yml` 的 `sign-rewards` 段配置驱动
- 实用命令：`/suicide`（自杀）、`/heal`（恢复生命与饱食度，权限 `hopecraft.heal`）、`/feed`（恢复饱食度，权限 `hopecraft.feed`）

## ⚙️ 硬性要求
| 组件            | 最低版本       | 推荐链接                     |  
|----------------|--------------|----------------------------|  
| **Java**       | JDK 25       | [Adoptium](https://adoptium.net/) |  
| **服务端核心**   | Paper 26.2 (Minecraft 26.2) | [PaperMC](https://papermc.io/) |  
| **构建工具**     | Maven 3.9+   | [Maven](https://maven.apache.org/) |  

## 🛠️ 如何构建（Linux/macOS（Windows建议使用Git Bash））
```
bash

git clone https://github.com/busysheng/hopecraft-ng.git

cd hopecraft-ng

mvn clean package -DskipTests # 产出位于 target/
```

## 📦 安装流程
1. 将 `target/HopeCraft-*.jar` 置于服务端 `plugins/`
2. **重启服务端**（首次加载必需）
3. 按需编辑生成的 `plugins/HopeCraft/config.yml`

## 🧩 项目结构
```
access transformers
HopeCraft-NG/

├── src/main/ # Java 业务逻辑

├── pom.xml # Maven 依赖及构建设置

└── target/ # 编译产出目录 (构建后生成)
```

## 文档
文档处于`./src/docs`中，目前写了签到奖励修改教程（`sign-rewards.md`）和物品对照表（`materials.md`）。

---
**核心维护**: [busysheng](https://github.com/busysheng)  
**旧版仓库（已EOL）**: [BusyMitten/HopeCraft](https://github.com/BusyMitten/HopeCraft)  
**最后更新**: 2026年8月20日
