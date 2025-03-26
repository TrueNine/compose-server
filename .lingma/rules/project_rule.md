# Kotlin Spring Boot 全栈开发规范

## 编程环境

- IDE: IntelliJ IDEA 2024.3.5 (Community Edition) or JetBrains Aqua 2024.3.5
- 操作系统: Windows 11 24H2
- 命令行工具：PowerShell 7.3.10
- windows 测试环境：wsl2 + ubuntu 24.04 LTS

## 目标实施环境

- 操作系统: Linux Ubuntu 24.04 LTS
- 配置：2核4G
- JDK: Playwright headless + OpenJDK 24
- 容器: Docker + Docker-Compose
- 数据库: PostgreSQL 17
- 缓存: Redis
- 对象存储: MinIO
- 反向代理: Nginx

## 基础配置

- 语言版本: Kotlin 2.1.0（优先）/Java 24（互操作）
- 框架版本: Spring Boot 3.4 + Jakarta EE 9
- 安全框架: Sa-Token 1.41.0
- 测试框架：JUnit 5 + Mockk + kotlin.test + spring boot test
- 测试数据库：h2
- 数据库: PostgreSQL 17.3 + Jimmer 0.9.64（强类型DSL）
- 缓存：Redis 7.3
- 容器技术: Docker + Docker-Compose

## 代码规范

### 命名规则

- 后缀规则：Controller类`Api`结尾，API路径含版本号（如`v1/user_account`）
- 分层命名：
  - Repository：`find`/`insert`/`update`/`delete`
  - Service：`fetch`/`post`/`remove`/`persist`
  - API：`get`/`post`/`put`/`delete`
- 禁用词：`ID`（统一使用`id`/`Id`）

### 开发实践

- 依赖注入：构造器注入优先，字段注入使用`@Resource`+lateinit
- 异常处理：禁用throw，强制使用`error`/`require`/`check`系列
- 实体规范：主键字段名为`id: RefId`（Long别名）
- 贫血模型：Controller仅做参数校验和结果返回

## 文档注释

### 结构规范

```kotlin
/**
 * # 类标题（H1）
 * > 功能概要
 *
 * 详细描述（支持Markdown）
 * @param 参数描述（data class参数需在此说明）
 */
class ExampleApi {
  /**
   * ## 方法标题
   * @param 使用Jimmer DSL操作数据库
   * @return 响应体结构说明
   */
  fun get() {
    // ...
  }
}
```

## 测试规范

- 方法命名：反引号包裹中文描述（`fun `测试 登录失败场景`()`）
- 依赖注入：`@Resource`注解于setter，禁用private
- 断言：统一使用kotlin.test断言库
- 事务：使用 `@Rollback` 确保测试用例不会污染数据库
- 测试数据：禁止在测试中准备数据，统一在 before 中 准备，在 after 中 清理

### 测试样例

```kotlin
@AutoConfigureMockMvc
@SpringBootTest
class ExampleApiTest {
  lateinit var mockMvc: MockMvc @Resource set
  lateinit var testData: Any

  @Test
  fun `测试 用户登录 失败后返回 403 响应码`() {
    // 测试逻辑
  }

  @BeforeTest
  fun setup() {
    // 在测试前准备需要的测试数据
  }

  @AfterTest
  fun after() {
    // 在测试结束后清理测试数据
  }
}

@SpringBootTest
class ExampleSpringServiceTest {
  lateinit var exampleSpringService: ExampleSpringService @Resource set
  lateinit var testData: Any

  @Test
  fun `确保 获取用户信息 不传入参数引发 IllegalArgumentException`() {
    // 测试逻辑
  }

  @BeforeTest
  fun setup() {
    // 在测试前准备需要的测试数据
  }

  @AfterTest
  fun after() {
    // 在测试结束后清理测试数据
  }
}
class ExampleFeatureTest {
  @Test
  fun `测试 jackson 序列化 kotlin Any 类型 无泛型参数 会导致异常`() {
    // 测试逻辑
  }

  @BeforeTest
  fun setup() {
    // 在测试前准备需要的测试数据
  }

  @AfterTest
  fun after() {
    // 在测试结束后清理测试数据
  }
}
```

## 附录：术语映射

| 缩写   | 全称    | 示例             |
|------|-------|----------------|
| dis  | 残疾    | disType=残疾类型   |
| cert | 证件    | certInfo=证件信息  |
| tax  | 税务    | taxVideo=个税视频  |
| spec | 查询参数  | specification  |
| wxpa | 微信公众号 | wxpaAuth=公众号认证 |

```
