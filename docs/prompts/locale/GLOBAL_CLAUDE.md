# CLAUDE.md

本文件为 Claude Code (claude.ai/code) 在所有代码仓库中工作时提供指导。

---

所有语言都必须遵循通用规则，特定语言在通用规则基础上还必须遵循特定语言规则。

# 通用规范

**强制规则**

1. 始终使用**简体中文**回复，即使用户输入大量英文提示，回复仍应使用简体中文
2. 禁止编写任何供用户使用的示例代码，即使临时测试需要，任务完成后也必须立即删除
3. 严禁通过简化问题来解决问题
4. 严禁通过降级依赖版本、减少测试断言、使用常量测试条件等方式解决编译错误和运行时错误
5. 严禁忽略异常或任何隐藏异常的行为
6. 严禁在代码中暴露 API 密钥、密码和令牌
7. 应积极按规则重构和修复上下文中出现的代码
8. 应积极使用日志来改进记录，主动补充缺失的日志
9. 允许在单元测试调试过程中添加日志以协助问题解决
10. 必须最大程度使用 `early return` 技术减少代码嵌套层级
11. 严禁在对话或任务完成后生成总结文档文件和其他不必要的操作
12. 严禁在除第三方库调用外的任何地方出现以下令人厌恶和颓废的术语
  + `util`, `utils`
  + `helper`, `helpers`
  + `tool`, `tools`

13. 严禁使用 `@Suppress`/`// @ts-ignore` 等注解抑制警告

**渐进式开发方法论**

- 采用测试驱动开发结合渐进式开发方式
- **婴儿步骤开发**：每次只编写少量代码，立即通过单元测试验证
- **快速反馈循环**：开发一点测试一点，绝不一次性编写大量代码
- **单独验证原则**：为多个代码编写单元测试时，逐一编写和验证，避免批量编写后统一验证
- **风险最小化**：通过频繁的小规模验证确保代码质量，避免错误积累
- **嵌套测试组织**：使用适当的分组，避免根级别出现大量独立测试方法
- 严禁出现没有断言的测试方法，发现时立即补充断言

**日志规范**

- 当没有日志时，应该使用更多日志来协助线上问题排查和获取更多上下文信息
- 排查问题时，可以向源代码添加更多日志以获取更详细的上下文信息
- 日志应仅使用英文记录，如发现其他语言应立即更正为英文
- 重点记录第三方库调用和外部API的详细日志，包括请求参数、响应结果、持续时间等
- 使用结构化日志格式：`log.info("API call completed - endpoint: {}, status: {}, duration: {}ms", endpoint, status, duration)`
- 记录关键上下文信息：用户ID、请求ID、操作类型、结果状态、异常详情
- 避免记录敏感信息：密码、API密钥、身份证号、手机号等
- 在外部调用、数据库操作、文件操作等关键点记录日志
- 使用占位符格式避免字符串拼接，保持日志消息简洁明了
- 合理使用日志级别，避免在生产环境输出过多DEBUG日志

**注释规则**

- 文档注释：必须使用英文注释
- 内部代码注释：必须使用中文注释
- 严禁使用行末注释

**测试组织最佳实践：**

- 为每个被测试的类/函数/变量/方法创建主要分组
- 按场景细分：正常案例、异常案例、边界案例
  + Kotlin示例：`@Nested inner class CreateUser { @Test fun should_create_successfully() {} }`

**代码风格**

- 代码缩进：使用2个空格缩进
- 代码换行：每个文件必须保留尾随换行符
- 文件编码：必须使用UTF-8编码
- 文件行结束符：必须使用LF行结束符
- 行长度：每行不得超过160个字符
- 配置参考：参考项目根目录中的`.editorconfig`配置

# 特定语言约定

**SQL规则**

- 严禁在SQL中使用任何注释
- 检查现有查询是否使用参数化
- 一致使用snake_case命名，即使在其他语言中出现的字符串也是如此

**JVM规则**

1. 严禁在测试代码中使用`@DisplayName`注解
2. 在spring/quarkus中，严禁使用框架特定注解，例如：必须用`@Resource`替换`@Autowired`
3. 尽可能最大程度使用项目JDK版本中可用的新特性
4. 禁止在单元测试中使用`@DisplayName`注解
5. 单元测试方法名应使用英文命名，下划线分隔
6. 禁止单元测试方法名以`test_`或`should_`开头
7. 记录日志时，使用格式`log.info("message param1: {}, param2{}")`
8. 禁止记录详细敏感信息，如：密码、身份证号、API密钥、手机号等
9. 接口必须以"I"前缀开头
10. 获取slf4j Logger实例的所有方式都必须是static、private和final的

**Java规则**

所有JVM平台编程语言：java、kotlin、groovy、scala都必须遵循这些原则

1. 可以使用`import xxx.*`导入来减少代码量，后续处理留给IDE
2. 尽可能使用JDK新特性
3. 变量声明应尽可能使用`final var`
4. 积极使用lambda表达式
5. 严禁使用`System.out.println`进行输出日志

**Kotlin规则**

1. 优先使用val进行不可变变量声明
2. 避免!!操作符，使用?.或let{}
3. 使用数据类替代多参数函数
4. 严禁使用`println`进行输出日志
5. 严禁在单元测试中使用`mockito`，使用`mockk`替代
6. 扩展方法和纯函数应该分离，不在同一文件中
  + 扩展函数文件命名约定：使用`*Extensions.kt`后缀
  + 纯函数文件命名约定：使用`*Functions.kt`后缀
7. 静态函数不能使用"of"但必须使用"operator fun get"并用@JvmStatic装饰
8. Logger实例获取必须用@JvmStatic装饰

**TypeScript规则**

- 启用严格模式和所有严格类型检查选项
- 启用strictNullChecks以防止null/undefined类型错误
- 禁止隐式any类型 (noImplicitAny)
- 禁止隐式this (noImplicitThis)
- 禁止隐式override (noImplicitOverride)
- 禁止未使用的局部变量 (noUnusedLocals)
- 禁止未使用的参数 (noUnusedParameters)
- 在catch变量中使用unknown类型 (useUnknownInCatchVariables)
- 严禁使用`// @ts-ignore`注释抑制类型错误
- 使用Bundler模块解析 (moduleResolution: "Bundler")
- 启用逐字模块语法 (verbatimModuleSyntax)
- 启用独立模块编译 (isolatedModules)
- 严禁混合JavaScript文件 (allowJs: false)
- 使用最新ES标准 (target: "ESNext", module: "ESNext")
- 必须生成类型声明文件 (declaration: true)
- 启用独立声明生成 (isolatedDeclarations)
- 开发中不输出文件，仅执行类型检查 (noEmit: true)
- 保留代码注释 (removeComments: false)
- 启用增量编译 (incremental: true)
- 跳过库文件类型检查以提高性能 (skipLibCheck: true)
- 优先类型推断，避免冗余的显式类型注解
- 函数参数和返回值必须有显式类型定义
- 优先使用interface而非type别名，对复杂类型使用type
- 严禁使用any类型，必要时使用unknown

**SCSS规则**

- 禁止使用`@import`，使用`@use`替代

**Markdown规则**

- 优化AI可读性和注意力而非人类消费
- 必须仅使用英文编写
- 嵌套列表应使用交替模式：- + - + 最多3层嵌套
- 保持内容简洁直接，避免不必要的冗长
- 最小化使用过多标题
