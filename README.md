# Compose Server

[![Kotlin](https://img.shields.io/badge/Kotlin-2.2.0-blue.svg)](https://kotlinlang.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.3-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Jimmer](https://img.shields.io/badge/Jimmer-0.9.102-orange.svg)](https://github.com/babyfish-ct/jimmer)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.truenine/composeserver-shared.svg)](https://central.sonatype.com/search?q=g:io.github.truenine)
[![License](https://img.shields.io/badge/License-LGPL%202.1-blue.svg)](LICENSE)

> 🚀 现代化的 Kotlin 企业级服务端框架库

一个功能完备、开箱即用的**模块化框架库**，采用现代化技术栈，通过 Maven/Gradle 依赖方式提供企业级的安全、缓存、存储、支付、AI等完整解决方案。已发布至 **Maven 中央仓库**
，支持按需选择模块集成到现有项目中。

## ✨ 核心特性

- 🎯 **现代化技术栈** - Kotlin 2.2.0 + Spring Boot 3.5.3 + Jimmer 0.9.102
- 🏗️ **模块化设计** - 15+ 核心模块，清晰的边界和依赖关系
- 📦 **对象存储** - MinIO、阿里云OSS、华为云OBS、火山引擎TOS 统一接口
- 🤖 **AI能力** - LangChain4j + MCP协议 + 多模型支持

## 🛠️ 技术栈

### 核心框架

- **[Kotlin](https://kotlinlang.org/)** 2.2.0 - 现代化JVM语言
- **[Spring Boot](https://spring.io/projects/spring-boot)** 3.5.3 - 企业级Java框架
- **[Jimmer](https://github.com/babyfish-ct/jimmer)** 0.9.102 - 现代化ORM框架
- **[Gradle](https://gradle.org/)** 9.x - 构建工具 + Version Catalog

### 对象存储

- **MinIO** - 私有云存储
- **阿里云OSS** - 公有云存储
- **华为云OBS** - 公有云存储
- **火山引擎TOS** - 公有云存储

## 🚀 快速开始

### 📦 Maven 中央仓库

所有模块已发布至 Maven 中央仓库：[**io.github.truenine**](https://central.sonatype.com/search?q=g:io.github.truenine)

### 1. 依赖引入

**Gradle (Kotlin DSL)**

```kotlin
// 核心基础模块
implementation("io.github.truenine:composeserver-shared:latest")

// 数据库模块（按需选择）
implementation("io.github.truenine:composeserver-rds-shared:latest")
// ...
```

**Maven**

```xml

<dependency>
  <groupId>io.github.truenine</groupId>
  <artifactId>composeserver-shared</artifactId>
  <version>latest</version>
</dependency>
```

### 🔄 版本管理

gradle 8.x 推荐使用 **Gradle Version Catalog** 统一管理版本：

[settings.gradle.kts]

```kotlin
dependencyResolutionManagement {
  versionCatalogs {
    // 添加外部新配置
    create("cs") { from("io.github.truenine:composeserver-version-catalog:latest") }
    // 自身已有的 配置（如果没有则忽略）
    create("libs") { from(files("gradle/libs.versions.toml")) }
  }
}
```

maven 推荐使用项目提供的 pom

[pom.xml]

```xml

<dependencyManagement>
  <dependencies>
    <dependency>
      <groupId>io.github.truenine</groupId>
      <artifactId>composeserver-bom</artifactId>
      <version>latest</version>
      <type>pom</type>
      <scope>import</scope>
    </dependency>
  </dependencies>
</dependencyManagement>
```

或使用 gradle platform 配置

```kotlin
dependencies {
  implementation(platform("io.github.truenine:composeserver-bom:latest"))
}
```

## 📚 文档和资源

### 📖 API 文档

- **Javadoc**: [在线 API 文档](https://javadoc.io/doc/io.github.truenine)
- **源码**: [GitHub 仓库](https://github.com/TrueNine/compose-server)

### 🔧 版本兼容性

| Compose Server | Spring Boot | Kotlin | JDK |
|----------------|-------------|--------|-----|
| 0.x.x          | 3.5.x       | 2.2.x  | 24+ |

## 📄 许可证

本项目采用 [GNU LESSER GENERAL PUBLIC LICENSE 2.1](LICENSE) 协议。

## 🙏 特别鸣谢

- [**Kotlin**](https://kotlinlang.org/) - 现代化的JVM语言，让开发更加优雅
- [**Spring Boot**](https://spring.io/projects/spring-boot) - 企业级Java框架，提供强大的基础设施
- [**Jimmer**](https://github.com/babyfish-ct/jimmer) - 现代化ORM框架，革命性的数据访问体验
- [**Gradle**](https://gradle.org/) - 强大的构建工具，支持复杂的模块化项目

- [**PostgreSQL**](https://www.postgresql.org/) - 世界上最先进的开源数据库
- [**Flyway**](https://flywaydb.org/) - 数据库版本管理工具
- [**HikariCP**](https://github.com/brettwooldridge/HikariCP) - 高性能的JDBC连接池
- [**Spring Security**](https://spring.io/projects/spring-security) - 强大的安全框架
- [**LangChain4j**](https://github.com/langchain4j/langchain4j) - Java的AI应用开发框架
- [**Ollama**](https://ollama.ai/) - 本地大模型运行平台
- [**EasyExcel**](https://github.com/alibaba/easyexcel) - 阿里巴巴的Excel处理工具
- [**Caffeine**](https://github.com/ben-manes/caffeine) - 高性能的Java缓存库
- [**MinIO**](https://min.io/) - 高性能的对象存储服务

⭐ 如果这个框架对你有帮助，请给我们一个星标！

[![Star History Chart](https://api.star-history.com/svg?repos=TrueNine/compose-server&type=Date)](https://star-history.com/#TrueNine/compose-server&Date)
