---
description: 
globs: *Test.kt,*Test.java
alwaysApply: false
---
- jUnit
- mockk
- spring-boot-test
- spring-mockmvc
- testcontainers
- jimmer
- redis
- postgresql

# 测试框架选择规则
1. 基础测试框架：
   - Kotlin项目优先使用 kotlin.test
   - Java项目使用 JUnit 5
2. Mock框架：
   - Kotlin项目优先使用 MockK
   - Java项目使用 Mockito
3. 集成测试框架：
   - 数据库测试必须使用 Testcontainers
   - Redis测试使用 Testcontainers + Redis模块
   - 消息队列测试使用对应的 Testcontainers 模块

# 测试命名规范
1. 类名：被测试类名 + Test
2. 方法名：使用反引号包裹，描述测试场景
   - 正向用例：`方法名 输入条件 返回预期结果`
   - 异常用例：`方法名 失败场景 抛出预期异常`

# 测试结构规范
1. 必需注解：
   ```kotlin
   @Test // 测试方法标记
   @BeforeTest // 测试前置准备
   @AfterTest // 测试后清理
   ```
2. Spring项目额外注解：
   ```kotlin
   @SpringBootTest // 需要Spring上下文时添加
   @AutoConfigureMockMvc // 测试Web接口时添加
   ```

# 依赖管理规则
1. 导入语句优先级：
   - kotlin.test 优先于直接使用 junit
   - jakarta 包优先于 javax 包
2. 测试依赖版本：
   - mockk: 1.13+
   - testcontainers: 1.19+
   - spring-boot-test: 与项目版本保持一致

```kotlin
package com.example

// 使用 kotlin.test 的别名而非直接的 junit or testng
import kotlin.test.Test
import kotlin.test.BeforeTest
import kotlin.test.AfterTest
// 使用jakarta而非javax
import jakarta.annotation.Resource
// ... 其他导入

@AutoConfigureMockMvc // 测试 spring webmvc接口则按需添加
@SpringBootTest // 测试涉及 spring bean 注入则按需添加
class ExampleApiTest { // 被测试类名称+Test
  lateinit var mockMvc: MockMvc @Resource set // 在 setter 使用 resource 注入
  lateinit var testData: Any

  @BeforeTest
  fun setup() {
    // 在测试前准备需要的测试数据
  }

  @AfterTest
  fun after() {
    // 在测试结束后销毁测试数据
  }

  @Test
  fun `getUserDatas 失败 后返回 403 响应码`() {
    // 测试逻辑
  }

  fun `postUserDatas 保证 接口幂等性`() {
    // 测试逻辑
  }
}
```

