# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## 框架定位

Compose Server 是一个现代化、模块化的 Kotlin 企业级服务端开发**框架**，而非脚手架。它通过 Gradle 多模块方式，提供安全、数据库、缓存、对象存储、支付、AI 等企业级能力，支持按需集成到任意
Spring Boot 项目中。

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

| 使用场景       | 推荐模块组合                          |
|------------|---------------------------------|
| 基础 Web API | shared + security-spring        |
| 数据库操作      | shared + rds-shared + rds-crud  |
| 文件存储       | shared + oss-shared + oss-minio |
| 微信支付       | shared + pay                    |
| 数据导入导出     | shared + data-extract           |
| AI 能力      | shared + mcp                    |

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

## Git 提交规范

### 提交消息格式

本项目采用表情符号 + 英文 Conventional Commits 格式，支持详细功能列表：

### 基础格式

```
emoji [scope] description
```

### 详细列表格式（可选）

```
emoji [scope] main feature description

- emoji specific change 1
- emoji specific change 2
- emoji specific change 3
```

**格式说明：**

- `emoji`：从下表中选择合适的表情符号
- `[scope]`：模块或功能范围，用方括号包围
- `description`：简洁的功能描述（支持中英文）
- **详细列表**：当改动较多时，可添加表情符号格式的具体变更列表

**使用原则：**

- 🎯 **简单改动**：仅使用基础格式
- 📋 **复杂改动**：使用详细列表格式，每项变更前加对应表情符号
- 🌐 **语言选择**：描述支持中英文，根据团队习惯选择

**基础格式示例：**

```
✨ [shared] add unified exception handling
🐛 [rds] fix connection pool issue
♻️ [security] refactor JWT validation
```

**详细列表格式示例：**

```
✨ [shared] 日志功能

- 🚑 紧急修复日志级别
- 🐛 依赖倒转缺失问题
- 💄 日志框架进行 inline 处理

🔧 [buildlogic] 构建系统优化

- ⚡ 提升编译性能
- 🔧 更新 Gradle 配置
- 📦 优化依赖管理
- 🚨 修复 Spotless 检查

♻️ [security] 安全模块重构

- 🏗️ 重构认证流程
- 🔐 增强权限控制
- 🛡️ 修复安全漏洞
- 📈 添加安全监控
```

### 表情符号参考表

| 表情符号 | 类型        | 描述       | 使用场景                 |
|------|-----------|----------|----------------------|
| 🎉   | feat      | 新功能/重大更新 | 添加新功能、重大特性、项目初始化     |
| ✨    | feat      | 新功能/增强   | 添加新特性、功能增强、文档更新      |
| 🐛   | fix       | Bug 修复   | 修复错误、解决问题            |
| 🔧   | config    | 配置修改     | 修改配置文件、CI/CD 配置、构建配置 |
| 📝   | docs      | 文档更新     | 更新文档、README、注释       |
| 🎨   | style     | 代码风格/格式化 | 代码格式化、样式调整、结构优化      |
| ♻️   | refactor  | 重构       | 代码重构、包结构调整、命名空间变更    |
| ⚡    | perf      | 性能优化     | 提升性能、优化算法            |
| 🔥   | remove    | 删除代码/文件  | 删除无用代码、移除功能、清理文件     |
| 🧪   | test      | 测试相关     | 添加测试、修复测试、测试配置       |
| 👷   | ci        | CI/CD 相关 | 持续集成、构建脚本、部署配置       |
| 📦   | build     | 构建系统     | 依赖管理、构建配置、打包相关       |
| ⬆️   | upgrade   | 升级依赖版本   | 升级依赖库版本、更新第三方库       |
| ⬇️   | downgrade | 降级依赖版本   | 降级依赖库版本、回退版本         |
| 🚀   | release   | 发布版本     | 版本发布、标签创建            |
| 🔀   | merge     | 合并分支     | 分支合并、冲突解决            |
| 🤖   | ai        | AI 工具配置  | AI 助手配置、自动化工具        |
| 💄   | optimize  | 优化/性能优化  | 性能优化、代码优化、算法改进       |
| 🌐   | network   | 网络相关     | 网络配置、API 调用、远程服务     |
| 🔐   | security  | 安全/防御编程  | 安全修复、权限控制、格式校验、防御编程  |
| 🚑   | hotfix    | 紧急修复     | 紧急修复、补救措施、临时解决方案     |
| 📈   | analytics | 分析/监控    | 性能监控、数据分析、日志记录       |
| 🍱   | assets    | 资源文件     | 图片、字体、静态资源           |
| 🚨   | lint      | 代码检查     | 修复 linting 警告、代码质量   |
| 💡   | comment   | 注释       | 添加或更新注释、文档字符串        |
| 🔊   | log       | 日志       | 添加日志、调试信息            |
| 🔇   | log       | 移除日志     | 删除日志、静默输出            |

### 功能类型详细说明

#### 🎉 重大功能 (Major Features)

- 🚀 项目初始化和重大里程碑
- 📦 新模块或子系统的引入
- 🏗️ 架构重大变更

#### ✨ 新功能 (Features)

- 🔌 新增 API 接口
- 🛠️ 新增工具类或组件
- ⚡ 功能增强和改进

#### 🐛 Bug 修复 (Bug Fixes)

- 🔨 修复功能错误
- 🔄 解决兼容性问题
- 🛡️ 修复安全漏洞

#### ♻️ 重构 (Refactoring)

- 🏗️ 代码结构优化
- 📁 包名或命名空间调整
- 🎯 设计模式应用

#### 🔧 配置 (Configuration)

- ⚙️ Gradle 构建配置
- 🔄 CI/CD 流水线配置
- 📋 环境配置文件

#### 📝 文档 (Documentation)

- 📖 README 更新
- 📚 API 文档
- 💬 代码注释完善

#### 💄 优化 (Optimization)

- ⚡ 性能优化
- 🔧 代码优化
- 📈 算法改进

#### 🌐 网络 (Network)

- 🔌 API 接口调用
- 🌍 远程服务集成
- 📡 网络配置

#### 🔐 安全/防御编程 (Security & Defensive Programming)

- 🛡️ 安全修复
- 🔒 权限控制
- ✅ 格式校验
- 🛠️ 防御编程

#### 🚑 紧急修复 (Hotfix)

- 🔥 紧急修复
- 🩹 补救措施
- ⚡ 临时解决方案

### Scope 范围说明

使用方括号 `[]` 包围 scope，从以下类别中选择合适的范围：

#### 🏗️ 模块名称 (Module Names)

```
[shared]      [rds]         [security]    [oss]
[pay]         [cacheable]   [data]        [testtoolkit]
[surveillance][meta]        [depend]      [gradle-plugin]
[ksp]         [sms]         [mcp]
```

#### 🎯 功能领域 (Functional Areas)

```
[auth]        [api]         [db]          [cache]
[config]      [test]        [build]       [ci]
[docs]        [core]        [utils]       [validation]
[logging]     [monitoring]  [migration]   [integration]
```

#### 🔧 特定组件 (Specific Components)

```
[gradle]      [buildlogic]  [deps]        [publish]
[release]     [claude]      [cursor]      [spotless]
[flyway]      [jimmer]      [spring]      [kotlin]
[docker]      [k8s]         [helm]        [terraform]
```

#### 🌐 技术栈 (Technology Stack)

```
[postgresql]  [redis]       [minio]       [oauth2]
[jwt]         [wechat]      [aliyun]      [huawei]
[tencent]     [ollama]      [langchain]   [ai]
```

#### 📁 文件类型 (File Types)

```
[readme]      [changelog]   [license]     [gitignore]
[dockerfile]  [compose]     [makefile]    [scripts]
```

### 提交规范要求

1. **表情符号使用**：每个提交必须以合适的表情符号开头，详细列表中每项也需要对应表情符号
2. **消息长度**：标题行不超过 72 个字符，详细列表每行不超过 50 个字符
3. **语言规范**：描述支持中英文，保持团队一致性
4. **列表使用**：

- 简单改动（1-2个变更）：使用基础格式
- 复杂改动（3个以上变更）：推荐使用详细列表格式
- 列表项按重要性或逻辑顺序排列

5. **原子性**：每个提交应该是一个逻辑上完整的变更单元
6. **测试验证**：提交前确保代码通过测试和格式检查

### 提交示例

#### 基础格式示例

```bash
# 简单新功能
git commit -m "✨ [shared] add unified exception handling"
git commit -m "🐛 [rds] fix connection pool issue"
git commit -m "♻️ [security] refactor JWT validation"
git commit -m "💄 [cache] optimize Redis performance"
git commit -m "🌐 [api] integrate third-party service"
git commit -m "🔐 [validation] add input format validation"
git commit -m "🚑 [auth] emergency fix for login issue"
git commit -m "📝 [docs] update API documentation"
```

#### 详细列表格式示例

```bash
# 复杂功能开发
git commit -m "✨ [shared] 日志功能

- 🚑 紧急修复日志级别
- 🐛 依赖倒转缺失问题
- 💄 日志框架进行 inline 处理"

# 性能优化
git commit -m "💄 [cache] 缓存系统优化

- ⚡ 提升 Redis 连接性能
- 🔧 优化缓存策略
- 📈 改进缓存命中率
- 🚨 修复内存泄漏问题"

# 网络集成
git commit -m "🌐 [api] 第三方服务集成

- 🔌 集成微信支付 API
- 📡 配置网络代理
- 🛠️ 添加重试机制
- 📊 网络监控埋点"

# 安全防御编程
git commit -m "🔐 [security] 安全防护增强

- 🛡️ 修复 SQL 注入漏洞
- ✅ 添加输入格式校验
- 🔒 增强权限控制
- 🛠️ 防御性编程实践"

# 多模块测试更新
git commit -m "🧪 [testtoolkit] 测试框架升级

- 🔧 更新 JUnit 配置
- 📦 集成 Testcontainers
- ⚡ 优化测试性能
- 📊 添加覆盖率报告"

# 版本发布和依赖管理
git commit -m "🚀 [release] version 0.0.5"
git commit -m "⬆️ [deps] upgrade Spring Boot to 3.5.0"
git commit -m "⬇️ [deps] downgrade Kotlin for compatibility"

# 清理和删除
git commit -m "🔥 [buildlogic] remove unused imports"
git commit -m "🔥 [EmptyDefault] remove related definitions"

# AI 工具配置
git commit -m "🤖 [claude] add AI configuration"
git commit -m "🤖 [lingma] add Tongyi Lingma support"
```

#### 混合格式示例

```bash
# 大版本发布（包含多项重要变更）
git commit -m "🚀 [release] version 1.0.0

- ✨ 新增统一异常处理
- 🔐 增强安全防护机制
- 💄 优化数据库连接池性能
- 🌐 集成第三方支付服务
- 📝 完善 API 文档
- 🧪 添加集成测试套件"

# 紧急修复（包含多个相关问题）
git commit -m "🚑 [security] 紧急安全修复

- 🛡️ 修复 JWT 验证漏洞
- 🔐 增强输入格式校验
- ✅ 添加防御性编程检查
- 📈 添加安全审计日志
- 🚨 更新安全检查规则"

# 性能优化专项（多个优化措施）
git commit -m "💄 [performance] 系统性能优化

- ⚡ 优化数据库查询性能
- 🔧 改进缓存算法
- 📈 提升 API 响应速度
- 🛠️ 优化内存使用"

# 网络功能增强
git commit -m "🌐 [network] 网络功能增强

- 🔌 集成新的 API 服务
- 📡 优化网络连接配置
- 🛠️ 添加网络异常处理
- 📊 网络性能监控"
```
