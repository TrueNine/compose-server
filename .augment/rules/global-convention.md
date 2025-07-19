---
type: "always_apply"
---

This file provides guidance to `Augment` when working with code in this Repository.

# Generic Standard

**Mandatory Convention**

1. Always respond in **Chinese-simplified**, Even if the user enters a large number of English prompts, they should return Chinese
2. It is forbidden to write any sample code for user use, even if it requires temporary testing, and delete it as soon as the task is complete
3. It is strictly forbidden to solve problems by simplifying them
4. It is strictly forbidden to downgrade a dependent version to resolve the issue
5. It is strictly forbidden to reduce test assertions to solve problems
6. Ignoring anomalies or any behavior that hides anomalies is strictly prohibited
7. Code that appears in the context should be actively refactored and repaired according to the rules，应当积极按照规则来重构修复
8. It is strictly forbidden to expose API keys, passwords, and tokens in the code
9. Logs should be actively used to complete logging, and missing logging should be actively supplemented
10. It is permissible to add logs during unit test debugging to assist in problem-solving
11. The usage of `ealry return` technique must be maximized to reduce code nesting levels
12. It is strictly forbidden to generate summary document files and other unnecessary operations after the conversation or task is completed

**Output Rules**

- **优先简洁直接的回复** - 避免冗长解释
- **批量相关工具调用** - 将多个信息请求合并为单次调用
- **使用高效工具序列** - 最小化冗余调用

**Comment Rules**

- 文档注释：英文注释
- 代码内部注释：英文注释，且解释"为什么"而非"做什么"

**TDD Convention**

1. TDD流程：失败测试→实现代码→重构
2. 覆盖边界条件和异常情况
3. 独立运行，无外部依赖
4. 测试命名清晰表达意图
5. 测试类与被测试类同名
6. **嵌套测试组织**：使用合适的分组，避免根级别大量独立测试方法

- 每个被测试类/函数/变量/方法创建主要分组
- 按场景细分：正常用例、异常用例、边界用例
- 示例kotlin：`@Nested inner class CreateUser { @Test fun should_create_successfully() {} }`

**DDD Convention**

- DDD：统一语言建模，聚合根维护不变性
- CQRS：命令查询分离
- EDA：事件驱动解耦

# Specific Language Conventions

**SQL Standard**

1. 检查现有查询是否使用参数化
2. 统一使用snake_case命名
3. 验证无字符串拼接风险

**JVM Standard**

1. 严谨在测试代码中使用 `@DisplayName` 注解
2. spring/quarkus 中严谨使用特定框架的注解，例如：`@Autowired`必须使用 `@Resource` 替代

**Java Standard**

1. 尽可能使用jdk的新特性
2. 声明变量应尽量使用 `final var`
3. 积极使用 lambda
4. 严禁使用 `System.out.println` 记录输出

**Kotlin Standard**

1. 优先使用val声明不可变变量
2. 避免!!操作符，使用?.或let{}
3. 数据类替代多参数函数
4. 严禁使用 `println` 记录输出

**TypeScript and Vue Standard**

- TypeScript: 启用strict模式，避免any类型
- Vue: 积极使用 vue3 新特性

# Git Commit Message Convention

**格式：** `emoji [scope] description`（简单）或详细列表格式（2+ 变更）

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

**提交示例：**
```bash
# 简单格式示例
✨ [shared] 添加统一异常处理

🐛 [rds] 修复连接池配置问题

♻️ [security] 重构JWT验证逻辑

# 复杂格式示例（注意列表中的表情符号）
✨ [ai] LangChain4j集成优化

- 🚑 修复模型加载超时问题
- 🐛 解决依赖冲突问题  
- 💄 优化AI服务性能
- 🧪 补充集成测试用例
```
