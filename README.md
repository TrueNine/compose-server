# Compose Server

一个快速且对开发者友好的服务端快速开发框架，采用多项目模块化设计，涵盖了大多数开发场景。

## git 地址

```text
https://github.com/TrueNine/compose-server (fetch)
https://codeup.aliyun.com/63fc0978360d441ff22c91e5/TrueNine/compose-server.git
https://codeup.aliyun.com/67396204e50481de876d1cf2/compose-server.git
```

## 项目特点

- 🚀 **快速开发**：基于 Spring Boot 和 Kotlin，提供高效的开发体验
- 📦 **模块化设计**：采用多项目模块化架构，便于维护和扩展
- 🔧 **统一版本管理**：使用 Gradle 版本目录统一管理依赖版本
- 🛠️ **丰富的功能**：内置常用功能模块，开箱即用
- 📚 **完善的文档**：提供详细的开发文档和使用指南

## 技术栈

- **核心框架**：[Spring Boot](https://spring.io/projects/spring-boot)
- **开发语言**：[Kotlin](https://kotlinlang.org/)
- **构建工具**：[Gradle](https://gradle.org/)
- **ORM框架**：[Jimmer](https://github.com/babyfish-ct/jimmer)

## 快速开始

- 使用 IntelliJ IDEA 或 Android Studio 打开项目
- 等待 Gradle 同步完成

## 项目结构

```
compose-server/
├── documentation/       # 项目文档
├── gradle/              # Gradle 配置
└── build.gradle.kts     # 项目构建配置
└── settings.gradle.kts  # 项目构建设置
```

## 文档

- [模块化设计说明](./documentation/model_manifest.md)
- [构建环境配置](./documentation/build_env.md)
- [依赖版本管理](./gradle/libs.versions.toml)

## 贡献指南

欢迎提交 Pull Request 或创建 Issue 来帮助改进项目。在提交代码前，请确保：

1. 代码符合项目的编码规范
2. 添加必要的测试用例
3. 更新相关文档

## 许可证

本项目采用 [GNU LESSER GENERAL PUBLIC LICENSE 2.1](/LICENSE) 协议。

## 特别鸣谢

- [Kotlin](https://kotlinlang.org/), [GitHub](https://github.com/JetBrains/kotlin)
- [Gradle](https://gradle.org/), [GitHub](https://github.com/gradle)
- [SpringBoot](https://spring.io/projects/spring-boot), [GitHub](https://github.com/spring-projects)
- [babyfish-ct jimmer](https://github.com/babyfish-ct), [GitHub](https://github.com/babyfish-ct/jimmer)
