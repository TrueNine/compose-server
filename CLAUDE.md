# CLAUDE.md - AI 助手系统提示

## 角色与身份
你是一个通过上下文引擎访问 Compose Server 代码库的智能编程助手。

**框架概述：** Compose Server 是现代化、模块化的 Kotlin 企业级开发框架（非脚手架），通过 Gradle 多模块提供企业级能力。所有模块可独立集成到任意 Spring Boot 项目中。

**技术栈：** Kotlin 2.2.x, Spring Boot 3.5.x, Jimmer 0.9.x, Gradle 9.x, PostgreSQL, Redis, Caffeine, MinIO, 云服务。

## Token 效率指南
- **优先简洁直接的回复** - 避免冗长解释
- **批量相关工具调用** - 将多个信息请求合并为单次调用
- **限制代码片段** 为关键行（`<augment_code_snippet>` 标签内最多 10 行）
- **使用高效工具序列** - 最小化冗余调用

## 模块结构与导航
**包格式：** `io.github.truenine.composeserver.{模块名}`

**核心模块：**
- `shared/` - 核心组件、工具类、异常处理、统一响应、分页
- `meta/` - 元数据/注解处理
- `rds/` - 数据库（Jimmer ORM、CRUD、PostgreSQL、Flyway）
- `surveillance/` - 监控
- `security/` - Spring Security、OAuth2、加密
- `oss/` - 对象存储（MinIO、云服务商）
- `pay/` - 支付（微信支付 V3）
- `cacheable/` - 多级缓存（Redis、Caffeine）

**扩展模块：**
- `data/` - 数据处理（Excel、爬虫、行政区划）
- `depend/` - 依赖处理
- `testtoolkit/` - 测试工具
- `gradle-plugin/` - Gradle 插件
- `ksp/` - Kotlin 符号处理
- `sms/` - 短信服务
- `mcp/` - AI 能力（LangChain4j、Ollama）

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
- `./gradlew spotlessApply` - 修复格式（提交前运行）
- `./gradlew versionCatalogUpdate` - 更新依赖

## 开发标准
- **依赖管理：** Gradle Version Catalog (`gradle/libs.versions.toml`)
- **插件约定：** 所有模块使用 `kotlinspring-convention`
- **代码格式：** Spotless（提交前必须）
- **测试命名：** 与被测试类同名，移除 `@DisplayName` 注解
- **集成：** 在 build.gradle.kts 中添加 `implementation("io.github.truenine:composeserver-{模块}:latest")`

## Git 提交规范

**格式：** `emoji [scope] description`（简单）或详细列表格式（3+ 变更）

**完整表情符号系统：**
| 表情符号 | 类型 | 描述 | 使用场景 |
|---------|------|------|----------|
| 🎉 | feat | 重大功能/初始化 | 新功能、重大更新、项目初始化 |
| ✨ | feat | 新功能/增强 | 添加功能、增强、文档更新 |
| 🐛 | fix | Bug 修复 | 修复错误、解决问题 |
| 🔧 | config | 配置修改 | 配置文件、CI/CD、构建配置 |
| 📝 | docs | 文档更新 | 更新文档、README、注释 |
| 🎨 | style | 代码风格/格式化 | 代码格式化、样式、结构优化 |
| ♻️ | refactor | 重构 | 代码重构、包结构调整 |
| ⚡ | perf | 性能优化 | 性能优化、算法改进 |
| 🔥 | remove | 删除代码/文件 | 删除无用代码、移除功能 |
| 🧪 | test | 测试相关 | 添加测试、修复测试、测试配置 |
| 👷 | ci | CI/CD | 持续集成、构建脚本 |
| 📦 | build | 构建系统 | 依赖管理、构建配置 |
| ⬆️ | upgrade | 升级依赖 | 升级库版本 |
| ⬇️ | downgrade | 降级依赖 | 降级库版本 |
| 🚀 | release | 发布版本 | 版本发布、标签创建 |
| 🔀 | merge | 合并分支 | 分支合并、冲突解决 |
| 🤖 | ai | AI 工具配置 | AI 助手配置、自动化 |
| 💄 | optimize | 优化 | 性能优化、代码改进 |
| 🌐 | network | 网络相关 | 网络配置、API 调用、远程服务 |
| 🔐 | security | 安全/验证 | 安全修复、权限控制、验证 |
| 🚑 | hotfix | 紧急修复 | 紧急修复、临时解决方案 |
| 📈 | analytics | 分析/监控 | 性能监控、数据分析 |
| 🍱 | assets | 资源文件 | 图片、字体、静态资源 |
| 🚨 | lint | 代码检查 | 修复 linting 警告、代码质量 |
| 💡 | comment | 注释 | 添加/更新注释、文档字符串 |
| 🔊 | log | 日志 | 添加日志、调试信息 |
| 🔇 | log | 移除日志 | 删除日志、静默输出 |

**作用域：** `[shared]` `[rds]` `[security]` `[oss]` `[pay]` `[cacheable]` `[data]` `[surveillance]` `[meta]` `[depend]` `[testtoolkit]` `[gradle-plugin]` `[ksp]` `[sms]` `[mcp]` `[auth]` `[api]` `[db]` `[cache]` `[config]` `[test]` `[build]` `[ci]` `[docs]` `[core]` `[utils]` `[validation]` `[gradle]` `[buildlogic]` `[deps]` `[spotless]` `[flyway]` `[jimmer]` `[spring]` `[kotlin]`

**规则：**
- 简单变更（1-2个）：基础格式
- 复杂变更（3+个）：详细列表格式
- 原子性提交，提交前测试
- 提交前运行 `./gradlew spotlessApply`

**示例：**
```bash
✨ [shared] 添加统一异常处理
🐛 [rds] 修复连接池问题
♻️ [security] 重构 JWT 验证

✨ [shared] 日志系统
- 🚑 修复日志级别紧急问题
- 🐛 解决依赖倒转问题
- 💄 优化日志框架内联处理
```
