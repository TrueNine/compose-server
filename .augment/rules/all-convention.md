---
type: "always_apply"
---

**框架概述：** Compose Server 是现代化、模块化的 Kotlin 企业级开发框架（非脚手架），通过 Gradle 多模块提供企业级 SDK。所有模块可独立集成到任意 Spring Boot 或其他 JVM 项目中。

**技术栈：** Kotlin 2.2.0, Spring Boot 3.5.3, Spring Framework 6.2.6, Jimmer 0.9.101, Gradle 9.0.0-rc-4, Java 24, PostgreSQL, Redis, Caffeine, MinIO, LangChain4j。

## 模块结构与导航
**包格式：** `io.github.truenine.composeserver.{模块名}`

**核心基础模块：**
- `shared/` - 核心组件、工具类、异常处理、统一响应、分页、类型定义
- `testtoolkit/` - 测试工具包、TestContainers集成
- `version-catalog/` - 版本目录管理
- `bom/` - 依赖管理清单
- `gradle-plugin/` - Gradle插件和约定

**业务能力模块：**
- `cacheable/` - 多级缓存（Redis、Caffeine）
- `ai/` - AI服务
  - `ai:shared` - AI共享组件
  - `ai:langchain4j` - LangChain4j集成
- `pay/` - 支付服务
  - `pay:shared` - 支付共享组件
  - `pay:wechat` - 微信支付V3
- `oss/` - 对象存储
  - `oss:shared` - OSS共享组件
  - `oss:minio` - MinIO集成
  - `oss:aliyun-oss` - 阿里云OSS
  - `oss:huawei-obs` - 华为云OBS
  - `oss:volcengine-tos` - 火山引擎TOS
- `rds/` - 关系型数据库
  - `rds:shared` - RDS共享组件
  - `rds:crud` - CRUD操作
  - `rds:jimmer-ext-postgres` - Jimmer PostgreSQL扩展
  - `rds:flyway-migration-postgresql` - Flyway PostgreSQL迁移

**系统服务模块：**
- `security/` - 安全服务
  - `security:spring` - Spring Security集成
  - `security:oauth2` - OAuth2支持
  - `security:crypto` - 加密组件
- `sms/` - 短信服务
  - `sms:shared` - 短信共享组件
  - `sms:tencent` - 腾讯云短信
- `surveillance/` - 监控服务
  - `surveillance:shared` - 监控共享组件
  - `surveillance:hikvision` - 海康威视集成

**数据处理模块：**
- `data/` - 数据处理
  - `data:crawler` - 网络爬虫
  - `data:extract` - 数据提取
- `depend/` - 依赖处理
  - `depend:servlet` - Servlet依赖
  - `depend:paho` - MQTT Paho客户端
  - `depend:http-exchange` - spring6 webexchange
  - `depend:jackson` - Jackson 处理
  - `depend:springdoc-openapi` - OpenAPI文档
  - `depend:xxl-job` - XXL-Job集成

**代码生成模块：**
- `ksp/` - Kotlin符号处理
  - `ksp:plugin` - KSP插件
  - `ksp:shared` - KSP共享组件
  - `ksp:meta` - 元数据定义

**常用路径：**
- 构建文件：`{模块}/build.gradle.kts`
- 源码：`{模块}/src/main/kotlin/io/github/truenine/composeserver/{模块}/`
- 测试：`{模块}/src/test/kotlin/`
- 资源：`{模块}/src/main/resources/`

## 构建命令
- `./gradlew build` - 构建项目
- `./gradlew clean` - 清理输出
- `./gradlew publishToMavenLocal` - 本地发布
- `./gradlew test` - 运行所有测试
- `./gradlew :{模块}:test` - 模块特定测试
- `./gradlew spotlessApply` - 修复格式（提交前必须运行）
- `./gradlew versionCatalogFormat` - 修复 `libs.versions.toml` 格式（提交前必须运行）

## 构建约定与插件
**build-logic 约定插件体系：**
- `buildlogic.jacoco-conventions` - 代码覆盖率约定
- `buildlogic.java-conventions` - Java约定
- `buildlogic.javaspring-conventions` - Java Spring约定
- `buildlogic.kotlin-conventions` - Kotlin约定
- `buildlogic.kotlinspring-conventions` - Kotlin Spring约定（主要使用）
- `buildlogic.publish-conventions` - 发布约定
- `buildlogic.repositories-conventions` - 仓库约定
- `buildlogic.spotless-conventions` - 代码格式化约定

## 开发标准
- **依赖管理：** Gradle Version Catalog (`gradle/libs.versions.toml`) 统一版本管理
- **插件约定：** 所有Kotlin模块使用 `kotlinspring-conventions`，Java模块使用相应约定
- **代码格式：** Spotless自动化格式检查（提交前必须运行 `./gradlew spotlessApply`）
- **测试规范：** 测试类与被测试类同名，使用@Nested组织测试，禁用@DisplayName注解
- **模块集成：** `implementation("io.github.truenine:composeserver-{模块}:0.0.10")`
- **Java版本：** 支持Java 24最新特性，无向下兼容，积极使用新特性
- **Kotlin约定：** 优先使用val、避免!!操作符、积极使用lambda和新特性

## 框架特定开发指导

**Spring Boot 3.x 约定：**
- 使用 `@Resource` 替代 `@Autowired` 进行依赖注入
- 配置类使用 `@EnableConfigurationProperties` 启用属性绑定
- 自动配置类命名为 `AutoConfigEntrance` 并使用 `@ComponentScan`
- 异常处理使用统一的 `ErrorResponseEntity` 响应格式
- 日志记录使用 `slf4j(ClassName::class)` 获取logger实例

**Jimmer ORM 约定：**
- 实体类使用 `@Entity` 注解，遵循 Jimmer 规范
- 查询使用 KSP 生成的类型安全查询 API
- 分页查询统一使用 `fetchPq()` 扩展函数
- 数据库函数扩展放在对应的 `*Fns.kt` 文件中
- 使用 `View` 接口定义 DTO 投影，通过 `toFetcher()` 获取 Fetcher

**Kotlin 2.2.0 特性使用：**
- 积极使用 `data class` 替代多参数函数
- 扩展函数命名使用动词形式，如 `hasText()`, `isNotEmptyRun()`
- 使用 `@OptIn(ExperimentalContracts::class)` 启用契约功能
- 运算符重载使用 `infix` 函数，如 `Pair.and()`
- 字符串模板优先使用 `${}` 语法

**测试框架约定：**
- 使用 TestContainers 进行集成测试
- 测试方法命名使用反引号中文描述：`fun \`测试用户创建成功\`()`
- 使用 `@Nested inner class` 组织测试场景
- 测试日志使用 `testtoolkit.log` 实例
- Mock 对象使用 `every { } returns` 语法
