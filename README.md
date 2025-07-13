# Compose Server

[![Kotlin](https://img.shields.io/badge/Kotlin-2.2.0-blue.svg)](https://kotlinlang.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.3-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Jimmer](https://img.shields.io/badge/Jimmer-0.9.99-orange.svg)](https://github.com/babyfish-ct/jimmer)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.truenine/composeserver-shared.svg)](https://central.sonatype.com/search?q=g:io.github.truenine)
[![License](https://img.shields.io/badge/License-LGPL%202.1-blue.svg)](LICENSE)

> 🚀 现代化的 Kotlin 企业级服务端框架库

一个功能完备、开箱即用的**模块化框架库**，采用现代化技术栈，通过 Maven/Gradle 依赖方式提供企业级的安全、缓存、存储、支付、AI等完整解决方案。已发布至 **Maven 中央仓库**
，支持按需选择模块集成到现有项目中。

## ✨ 核心特性

- 🎯 **现代化技术栈** - Kotlin 2.2.0 + Spring Boot 3.5.3 + Jimmer 0.9.97
- 🏗️ **模块化设计** - 15+ 核心模块，清晰的边界和依赖关系
- 🔐 **企业级安全** - Spring Security + JWT + OAuth2 + 加密解密
- 💾 **多数据库支持** - PostgreSQL + Jimmer ORM + Flyway 迁移
- 📦 **对象存储** - MinIO、阿里云OSS、华为云OBS 统一接口
- 💰 **支付集成** - 微信支付V3 API 完整支持
- 🤖 **AI能力** - LangChain4j + MCP协议 + 多模型支持
- 🔄 **多级缓存** - Redis + Caffeine 高性能缓存
- 📊 **数据处理** - EasyExcel + 爬虫 + 数据提取
- 📱 **短信服务** - 腾讯云短信 + 可扩展架构

## 🛠️ 技术栈

### 核心框架

- **[Kotlin](https://kotlinlang.org/)** 2.2.0 - 现代化JVM语言
- **[Spring Boot](https://spring.io/projects/spring-boot)** 3.5.3 - 企业级Java框架
- **[Jimmer](https://github.com/babyfish-ct/jimmer)** 0.9.97 - 现代化ORM框架
- **[Gradle](https://gradle.org/)** 9.x - 构建工具 + Version Catalog

### 数据库和持久化

- **PostgreSQL** - 主数据库
- **Redis** - 缓存和会话存储
- **Flyway** - 数据库版本管理
- **HikariCP** - 高性能连接池

### 安全和认证

- **Spring Security** - 安全框架
- **JWT** - 无状态认证
- **OAuth2** - 第三方登录
- **OWASP AntiSamy** - XSS防护

### 对象存储

- **MinIO** - 私有云存储
- **阿里云OSS** - 公有云存储
- **华为云OBS** - 公有云存储

### AI和机器学习

- **[LangChain4j](https://github.com/langchain4j/langchain4j)** - AI应用开发
- **Ollama** - 本地大模型
- **智谱AI** - 云端AI服务

## 📦 发布模块

### 🏷️ Maven 模块列表

以下是已发布到 Maven 中央仓库的所有模块：

#### 核心模块

- **`composeserver-shared`** - 核心基础组件（必选）
  - 统一异常处理、响应封装、分页查询
  - 通用工具类、常量定义、枚举类型
  - 基础实体类和审计字段

#### 数据库模块

- **`composeserver-rds-shared`** - 数据库共享组件
- **`composeserver-rds-crud`** - CRUD 操作封装
- **`composeserver-rds-jimmer-ext-postgres`** - Jimmer PostgreSQL 扩展

#### 安全模块

- **`composeserver-security-spring`** - Spring Security 集成
- **`composeserver-security-oauth2`** - OAuth2 认证
- **`composeserver-security-crypto`** - 加密解密功能

#### 对象存储模块

- **`composeserver-oss-shared`** - 对象存储共享组件
- **`composeserver-oss-minio`** - MinIO 集成
- **`composeserver-oss-aliyun-oss`** - 阿里云 OSS 集成
- **`composeserver-oss-huawei-obs`** - 华为云 OBS 集成

#### 功能模块

- **`composeserver-cacheable`** - 缓存组件
- **`composeserver-pay`** - 支付模块（微信支付）
- **`composeserver-sms`** - 短信发送模块
- **`composeserver-data-extract`** - 数据提取（EasyExcel）
- **`composeserver-data-crawler`** - 数据爬虫
- **`composeserver-mcp`** - AI 相关模块

### 🔗 模块依赖关系

```
composeserver-shared (核心)
    ├── composeserver-rds-shared
    │   ├── composeserver-rds-crud
    │   └── composeserver-rds-jimmer-ext-postgres
    ├── composeserver-security-spring
    │   ├── composeserver-security-oauth2
    │   └── composeserver-security-crypto
    ├── composeserver-oss-shared
    │   ├── composeserver-oss-minio
    │   ├── composeserver-oss-aliyun-oss
    │   └── composeserver-oss-huawei-obs
    └── 其他功能模块...
```

### 📋 模块选择指南

| 使用场景           | 推荐模块组合                                |
|----------------|---------------------------------------|
| **基础 Web API** | `shared` + `security-spring`          |
| **数据库操作**      | `shared` + `rds-shared` + `rds-crud`  |
| **文件存储**       | `shared` + `oss-shared` + `oss-minio` |
| **微信支付**       | `shared` + `pay`                      |
| **数据导入导出**     | `shared` + `data-extract`             |
| **AI 功能**      | `shared` + `mcp`                      |

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
implementation("io.github.truenine:composeserver-rds-crud:latest")
implementation("io.github.truenine:composeserver-rds-jimmer-ext-postgres:latest")

// 安全模块（按需选择）
implementation("io.github.truenine:composeserver-security-spring:latest")
implementation("io.github.truenine:composeserver-security-oauth2:latest")
implementation("io.github.truenine:composeserver-security-crypto:latest")

// 对象存储模块（按需选择）
implementation("io.github.truenine:composeserver-oss-minio:latest")
implementation("io.github.truenine:composeserver-oss-aliyun-oss:latest")
implementation("io.github.truenine:composeserver-oss-huawei-obs:latest")

// 其他功能模块
implementation("io.github.truenine:composeserver-cacheable:latest")
implementation("io.github.truenine:composeserver-pay:latest")
implementation("io.github.truenine:composeserver-sms:latest")
implementation("io.github.truenine:composeserver-data-extract:latest")
implementation("io.github.truenine:composeserver-mcp:latest")
```

**Maven**

```xml
<!-- 核心基础模块 -->
<dependency>
  <groupId>io.github.truenine</groupId>
  <artifactId>composeserver-shared</artifactId>
  <version>latest</version>
</dependency>

  <!-- 数据库模块 -->
<dependency>
<groupId>io.github.truenine</groupId>
<artifactId>composeserver-rds-shared</artifactId>
<version>latest</version>
</dependency>

  <!-- 安全模块 -->
<dependency>
<groupId>io.github.truenine</groupId>
<artifactId>composeserver-security-spring</artifactId>
<version>latest</version>
</dependency>
```

### 🔄 版本管理

推荐使用 **Gradle Version Catalog** 统一管理版本：

```toml
# gradle/libs.versions.toml
[versions]
composeserver = "latest"

[libraries]
composeserver-shared = { module = "io.github.truenine:composeserver-shared", version.ref = "composeserver" }
composeserver-rds-shared = { module = "io.github.truenine:composeserver-rds-shared", version.ref = "composeserver" }
composeserver-security-spring = { module = "io.github.truenine:composeserver-security-spring", version.ref = "composeserver" }
```

### 2. 基本配置

```yaml
# application.yml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/compose_server
    username: postgres
    password: password

  # Redis配置
  redis:
    host: localhost
    port: 6379

  # 缓存配置
  cache:
    type: redis
    redis:
      time-to-live: 3600000

# Jimmer配置
jimmer:
  show-sql: true
  pretty-sql: true
  database-validation-mode: WARNING
```

### 3. 集成示例

#### 在现有 Spring Boot 项目中集成

**1. 添加依赖**

```kotlin
// build.gradle.kts
dependencies {
  implementation("io.github.truenine:composeserver-shared:latest")
  implementation("io.github.truenine:composeserver-rds-shared:latest")
  implementation("io.github.truenine:composeserver-security-spring:latest")
}
```

**2. 启用自动配置**

```kotlin
@SpringBootApplication
@EnableComposeServer  // 启用 Compose Server 自动配置
class YourApplication

fun main(args: Array<String>) {
  runApplication<YourApplication>(*args)
}
```

**3. 使用框架功能**

```kotlin
@RestController
@RequestMapping("/api/users")
class UserController(
  private val userService: UserService
) {
  // 使用框架提供的分页封装
  @GetMapping
  fun getUsers(@RequestParam page: Int = 0): ComposePageResult<User> {
    return userService.findAll(ComposePageRequest.of(page, 10))
  }

  // 使用框架提供的统一响应格式
  @PostMapping
  fun createUser(@RequestBody user: User): ComposeResult<User> {
    return ComposeResult.success(userService.save(user))
  }

  // 使用框架提供的异常处理
  @GetMapping("/{id}")
  fun getUser(@PathVariable id: Long): ComposeResult<User> {
    val user = userService.findById(id)
      ?: throw ComposeBusinessException("用户不存在")
    return ComposeResult.success(user)
  }
}
```

**4. 数据库实体（使用 Jimmer）**

```kotlin
@Entity
@Table(name = "users")
interface User : ComposeBaseEntity {
  val username: String
  val email: String
  val roles: List<Role>
}
```

## 🔧 构建命令

### 基本构建

```bash
# 构建整个项目
./gradlew build

# 清理构建输出
./gradlew clean

# 发布到本地Maven仓库
./gradlew publishToMavenLocal
```

### 测试

```bash
# 运行所有测试
./gradlew test

# 运行特定模块测试
./gradlew :shared:test
./gradlew :rds:shared:test
```

### 代码质量

```bash
# 检查代码格式
./gradlew spotlessCheck

# 自动修复代码格式
./gradlew spotlessApply
```

### 版本管理

```bash
# 更新版本目录中的依赖版本
./gradlew versionCatalogUpdate
```

## 🏗️ 核心模块详解

### 📦 shared - 核心共享组件

提供通用工具类、异常处理、类型定义等基础功能，是所有模块的基础依赖。

**主要功能：**

- 统一异常处理和响应封装
- 通用工具类（日期、字符串、集合等）
- 基础实体类和审计字段
- 分页查询封装
- 常量定义和枚举类型

### 🗄️ rds - 数据库模块

基于Jimmer ORM的数据库操作封装，提供强类型查询和自动代码生成。

**主要功能：**

- Jimmer ORM PostgreSQL扩展
- CRUD操作统一封装
- 数据库连接池配置
- Flyway数据库迁移
- 多租户支持

### 🔐 security - 安全模块

完整的企业级安全解决方案，支持多种认证方式。

**主要功能：**

- Spring Security集成
- JWT无状态认证
- OAuth2第三方登录
- 加密解密工具
- XSS防护

### 📁 oss - 对象存储模块

统一的对象存储接口，支持多个云厂商。

**主要功能：**

- MinIO私有云存储
- 阿里云OSS集成
- 华为云OBS集成
- 统一的文件操作接口
- S3策略管理

### 💰 pay - 支付模块

完整的微信支付V3 API集成。

**主要功能：**

- 微信支付统一下单
- 支付回调处理
- 证书自动管理
- 退款处理

### 🤖 mcp - AI模块

基于LangChain4j的AI能力集成。

**主要功能：**

- LangChain4j集成
- Ollama本地模型支持
- 智谱AI云端服务
- MCP协议支持

### 📊 data - 数据处理模块

强大的数据处理和提取能力。

**主要功能：**

- EasyExcel大文件处理
- 中国行政区划数据
- 数据爬虫功能
- 7zip压缩支持

## 🌟 特色功能

### 🔄 多级缓存

- **Redis** - 分布式缓存
- **Caffeine** - 本地缓存
- **Spring Cache** - 注解式缓存
- 自动缓存预热和过期策略

### 📱 短信服务

- **腾讯云短信** - 完整API集成
- **模板管理** - 短信模板统一管理
- **发送记录** - 完整的发送日志
- **扩展设计** - 易于接入其他厂商

### 🔍 数据提取

- **行政区划** - 完整的中国行政区划数据
- **身份证解析** - 身份证号码验证和信息提取
- **姓名数据** - 中文姓名数据库
- **地理坐标** - 坐标系统转换

### 🧪 测试工具

- **Testcontainers** - 容器集成测试
- **JUnit 5** - 单元测试
- **Spring Test** - Spring测试支持

## 📚 文档和资源

### 📖 API 文档

- **Javadoc**: [在线 API 文档](https://javadoc.io/doc/io.github.truenine)
- **源码**: [GitHub 仓库](https://github.com/TrueNine/compose-server)

### 🔧 版本兼容性

| Compose Server | Spring Boot | Kotlin | JDK |
|----------------|-------------|--------|-----|
| 0.x.x          | 3.5.x       | 2.2.x  | 24+ |

### 📝 变更日志

- **最新版本**: 查看 [Releases](https://github.com/TrueNine/compose-server/releases)
- **升级指南**: 参考各版本发布说明

## 📄 许可证

本项目采用 [GNU LESSER GENERAL PUBLIC LICENSE 2.1](LICENSE) 协议。

## 🙏 特别鸣谢



#### 核心技术栈

- [**Kotlin**](https://kotlinlang.org/) - 现代化的JVM语言，让开发更加优雅
- [**Spring Boot**](https://spring.io/projects/spring-boot) - 企业级Java框架，提供强大的基础设施
- [**Jimmer**](https://github.com/babyfish-ct/jimmer) - 现代化ORM框架，革命性的数据访问体验
- [**Gradle**](https://gradle.org/) - 强大的构建工具，支持复杂的模块化项目

#### 数据库和持久化

- [**PostgreSQL**](https://www.postgresql.org/) - 世界上最先进的开源数据库
- [**Redis**](https://redis.io/) - 高性能的内存数据库
- [**Flyway**](https://flywaydb.org/) - 数据库版本管理工具
- [**HikariCP**](https://github.com/brettwooldridge/HikariCP) - 高性能的JDBC连接池

#### 安全和认证

- [**Spring Security**](https://spring.io/projects/spring-security) - 强大的安全框架
- [**OWASP AntiSamy**](https://github.com/nahsra/antisamy) - XSS防护工具
- [**Hutool**](https://hutool.cn/) - 优秀的Java工具类库

#### AI和机器学习

- [**LangChain4j**](https://github.com/langchain4j/langchain4j) - Java的AI应用开发框架
- [**Ollama**](https://ollama.ai/) - 本地大模型运行平台

#### 工具和库

- [**EasyExcel**](https://github.com/alibaba/easyexcel) - 阿里巴巴的Excel处理工具
- [**Caffeine**](https://github.com/ben-manes/caffeine) - 高性能的Java缓存库
- [**MinIO**](https://min.io/) - 高性能的对象存储服务

---

⭐ 如果这个框架对你有帮助，请给我们一个星标！

[![Star History Chart](https://api.star-history.com/svg?repos=TrueNine/compose-server&type=Date)](https://star-history.com/#TrueNine/compose-server&Date)
