# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## 框架定位

Compose Server 是一个现代化、模块化的 Kotlin 企业级服务端开发**框架**，而非脚手架。它通过 Gradle 多模块方式，提供安全、数据库、缓存、对象存储、支付、AI 等企业级能力，支持按需集成到任意 Spring Boot 项目中。

## 构建和测试命令

这是一个基于 Gradle 的 Kotlin 多模块项目。

- `./gradlew build` - 构建整个项目
- `./gradlew clean` - 清理构建输出
- `./gradlew publishToMavenLocal` - 发布到本地 Maven 仓库
- `./gradlew versionCatalogUpdate` - 更新版本目录中的依赖版本
- `./gradlew test` - 运行所有测试
- `./gradlew :模块名:test` - 运行特定模块的测试
- `./gradlew spotlessCheck` - 检查代码格式
- `./gradlew spotlessApply` - 自动修复代码格式

## 项目架构

### 模块化结构

本框架采用多模块设计，主要模块包括：

- **shared** - 核心基础组件，包含通用工具类、异常处理、类型定义、统一响应、分页等
- **meta** - 元数据和注解处理器
- **rds** - 数据库相关（Jimmer ORM、CRUD、PostgreSQL 扩展、Flyway 迁移）
- **surveillance** - 监控组件
- **security** - 安全相关（Spring Security、OAuth2、加密解密）
- **oss** - 对象存储（MinIO、阿里云 OSS、华为云 OBS）
- **pay** - 支付模块（微信支付 V3）
- **cacheable** - 多级缓存（Redis、Caffeine）
- **data** - 数据处理（EasyExcel、爬虫、行政区划等）
- **depend** - 特定依赖处理
- **testtoolkit** - 测试工具包
- **gradle-plugin** - Gradle 插件
- **ksp** - Kotlin Symbol Processing
- **sms** - 短信服务（腾讯云短信，短信抽象层）
- **mcp** - AI 能力（LangChain4j、Ollama、智谱 AI）

> 所有模块均可独立集成，推荐组合见下表。

### 推荐模块组合

| 使用场景         | 推荐模块组合                                 |
|----------------|----------------------------------------|
| 基础 Web API   | shared + security-spring                |
| 数据库操作     | shared + rds-shared + rds-crud          |
| 文件存储       | shared + oss-shared + oss-minio         |
| 微信支付       | shared + pay                            |
| 数据导入导出   | shared + data-extract                   |
| AI 能力        | shared + mcp                            |

## 技术栈

- **Kotlin** 2.2.x
- **Spring Boot** 3.5.x
- **Jimmer** 0.9.x
- **Gradle** 9.x
- **PostgreSQL**、**Redis**、**Caffeine**、**MinIO**、**阿里云 OSS**、**华为云 OBS** 等

## 依赖管理

- 统一使用 Gradle Version Catalog（`gradle/libs.versions.toml`）管理依赖版本
- 所有模块版本、groupId 通过根项目统一管理
- 推荐通过 `publishToMavenLocal` 集成本地开发版本


## 代码约定

- 所有模块使用 `kotlinspring-convention` 插件，集成 Spring Boot 与 Kotlin 规范
- 包名格式：`io.github.truenine.composeserver.模块名`

- 代码格式化：使用 Spotless，提交前请运行 `./gradlew spotlessApply`
- 数据库迁移：使用 Flyway，脚本位于 `rds/flyway-migration-数据库类型/src/main/resources/db/migration/`，命名规则 `V版本号__描述.sql`

## 集成与最佳实践

1. **依赖引入**  
   在业务项目的 `build.gradle.kts` 中按需添加依赖，例如：
   ```kotlin
   implementation("io.github.truenine:composeserver-shared:latest")
   implementation("io.github.truenine:composeserver-rds-shared:latest")
   implementation("io.github.truenine:composeserver-security-spring:latest")
   ```
2. **自动配置**  
   启用自动配置注解（如有）：
   ```kotlin
   @SpringBootApplication
   @EnableComposeServer
   class YourApplication
   ```
3. **统一响应、异常、分页等**  
   推荐使用框架内置的统一响应、异常处理、分页等能力，详见 `shared` 模块。

4. **测试与发布**
  - 修改代码后先格式化，再运行测试，最后构建或发布到本地 Maven 仓库
  - 推荐使用 `./gradlew test`、`./gradlew build`、`./gradlew publishToMavenLocal`

## 其他说明

- 本项目为**框架库**，不包含脚手架或项目初始化功能
- 所有模块均已发布至 Maven Central，详见 [README.md] 或 [Maven Central](https://central.sonatype.com/search?q=g:io.github.truenine)
- 详细 API、集成示例、变更日志等请参考 [README.md] 和官方文档

---

如需为本项目贡献代码或扩展模块，请遵循上述规范和最佳实践。
