# ⚡适用于 Bukkit 的轻量级 BlockBench 模型引擎
![Maven Central 版本](https://img.shields.io/maven-central/v/io.github.toxicity188/bettermodel?style=flat-square&logo=sonatype)
[![CodeFactor 评分](https://www.codefactor.io/repository/github/toxicity188/bettermodel/badge?style=flat-square)](https://www.codefactor.io/repository/github/toxicity188/bettermodel)
[![代码总行数](https://tokei.rs/b1/github/toxicity188/BetterModel?category=code&style=flat-square)](https://github.com/toxicity188/BetterModel)
[![Discord 链接](https://img.shields.io/badge/Discord-%235865F2.svg?style=flat-square&logo=discord&logoColor=white)](https://discord.com/invite/rePyFESDbk)
[![GitHub 议题/拉取请求](https://img.shields.io/github/issues/toxicity188/BetterModel?style=flat-square&logo=github)](https://github.com/toxicity188/BetterModel/issues)
[![bStats 服务器数量](https://img.shields.io/bstats/servers/24237?style=flat-square&logo=minecraft&label=bStats&color=0%2C150%2C136%2C0)](https://bstats.org/plugin/bukkit/BetterModel/24237)

***
![](https://github.com/user-attachments/assets/5a6c1a8c-6fe2-4a67-a10e-e63e40825d35)
![](https://github.com/user-attachments/assets/ff515577-6a72-48ba-9943-81f00dddb375)
***

该插件通过物品显示数据包（item-display packet）实现了**完全基于服务器端的 3D 模型**功能。

- 导入通用的 BlockBench 模型文件（.bbmodel 格式）
- 自动生成资源包
- 播放动画
- 与基础实体同步
- 自定义碰撞箱
- 支持 12 骨骼玩家动画

# 🔧构建信息
### 依赖库
- [Kotlin 标准库](https://github.com/JetBrains/kotlin)：现代函数式编程工具
- [semver4j](https://github.com/vdurmont/semver4j)：语义化版本解析工具
- [CommandAPI](https://github.com/CommandAPI/CommandAPI)：命令处理工具
- [adventure](https://github.com/KyoriPowered/adventure)：组件处理库
- [stable player display](https://github.com/bradleyq/stable_player_display)：玩家动画显示工具
- [expiringmap](https://github.com/jhalterman/expiringmap)：带过期功能的并发映射缓存
- [DynamicUV](https://github.com/toxicity188/DynamicUV)：玩家模型纹理坐标（UV）处理工具
- [molang-compiler](https://github.com/Ocelot5836/molang-compiler)：Molang 表达式编译与评估工具

### 构建命令
`./gradlew build`：构建所有 JAR 文件  
`./gradlew shadowJar`：构建插件 JAR 文件  
`./gradlew javadocJar`：构建 Javadoc 文档 JAR 文件

> [!NOTE]
> 该项目需要 Java 21 环境

# API（应用程序编程接口）
我们使用 Maven Central 和 GitHub Packages 作为依赖仓库。  
你可以在 [此处](https://github.com/toxicity188/BetterModel/wiki/API-example) 查看 API 使用示例。

### 正式版本（Release）
```kotlin
repositories {
    mavenCentral()
}

dependencies {
    compileOnly("io.github.toxicity188:bettermodel:VERSION")
}
```

### 快照版本（Snapshot）
```kotlin
repositories {
    maven("https://maven.pkg.github.com/toxicity188/BetterModel")
}

dependencies {
    compileOnly("io.github.toxicity188:bettermodel:VERSION-SNAPSHOT")
}
```

***
[!["请我喝杯咖啡"](https://www.buymeacoffee.com/assets/img/custom_images/orange_img.png)](https://buymeacoffee.com/toxicity188)
