# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## 构建和测试命令

这是一个基于 Gradle 的 Kotlin 多模块项目。

### 基本构建命令
- `./gradlew build` - 构建整个项目
- `./gradlew clean` - 清理构建输出
- `./gradlew publishToMavenLocal` - 发布到本地 Maven 仓库
- `./gradlew versionCatalogUpdate` - 更新版本目录中的依赖版本

### 测试命令
- `./gradlew test` - 运行所有测试
- `./gradlew :模块名:test` - 运行特定模块的测试
- `./gradlew :shared:test` - 运行共享模块测试
- `./gradlew :rds:shared:test` - 运行 RDS 共享模块测试

### 代码质量检查
- `./gradlew spotlessCheck` - 检查代码格式
- `./gradlew spotlessApply` - 自动修复代码格式

### 单个模块构建
- `./gradlew :模块名:build` - 构建特定模块
- `./gradlew :shared:build` - 构建共享模块

## 项目架构

### 模块化结构
项目采用多模块设计，主要模块包括：

- **shared** - 共享基础组件，包含通用工具类、异常处理、类型定义等
- **meta** - 元数据和注解处理器
- **rds** - 数据库相关模块
  - `rds:shared` - 数据库共享组件
  - `rds:crud` - CRUD 操作封装
  - `rds:jimmer-ext-postgres` - Jimmer ORM PostgreSQL 扩展
  - `rds:flyway-migration-postgresql` - Flyway 数据库迁移
- **security** - 安全相关模块
  - `security:spring` - Spring Security 集成
  - `security:oauth2` - OAuth2 认证
  - `security:crypto` - 加密解密功能
- **oss** - 对象存储模块
  - `oss:shared` - 对象存储共享组件
  - `oss:minio` - MinIO 集成
  - `oss:aliyun-oss` - 阿里云 OSS 集成
  - `oss:huawei-obs` - 华为云 OBS 集成
- **pay** - 支付模块（微信支付等）
- **cacheable** - 缓存组件
- **data** - 数据处理模块
  - `data:extract` - 数据提取（EasyExcel 等）
  - `data:crawler` - 数据爬虫
- **depend** - 特定依赖处理模块
- **testtoolkit** - 测试工具包
- **gradle-plugin** - Gradle 插件
- **ksp** - Kotlin Symbol Processing 相关
- **sms** - 短信发送模块
- **mcp** - AI 相关模块

### 技术栈
- **语言**: Kotlin (JVM)
- **框架**: Spring Boot 3.5.3
- **ORM**: Jimmer 0.9.97
- **构建工具**: Gradle 8.x
- **数据库**: PostgreSQL (主要)
- **缓存**: Redis, Caffeine
- **对象存储**: MinIO, 阿里云 OSS, 华为云 OBS

### 依赖管理
- 使用 Gradle Version Catalog (`gradle/libs.versions.toml`) 统一管理依赖版本
- 通过 buildSrc 自定义 Gradle 插件和约定
- 所有模块版本通过 `compose` 版本号统一管理

### 代码约定
- 所有模块使用 `kotlinspring-convention` 插件，集成 Spring Boot 和 Kotlin 配置
- 测试类命名为 `TestEntrance`
- 使用 Spotless 进行代码格式化
- 包名遵循 `net.yan100.compose.模块名` 格式

### 开发工作流
1. 修改代码后运行 `./gradlew spotlessApply` 格式化代码
2. 运行 `./gradlew test` 确保测试通过
3. 运行 `./gradlew build` 构建项目
4. 使用 `./gradlew publishToMavenLocal` 发布到本地测试

### 数据库迁移
- 使用 Flyway 进行数据库版本管理
- 迁移脚本位于 `rds/flyway-migration-postgresql/src/main/resources/db/migration/`
- 命名规则：`V版本号__描述.sql`