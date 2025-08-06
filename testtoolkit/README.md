# Test Toolkit

测试工具包，提供测试期间的自动配置和工具函数。

## 功能特性

### 自动配置

- **关闭条件评估报告**：自动关闭 Spring Boot 的条件评估报告，减少测试日志噪音
- **关闭 Banner**：自动关闭 Spring Boot 启动 Banner，简化测试输出
- **启用虚拟线程**：默认开启 JDK 虚拟线程，提升测试性能
- **ANSI 颜色输出**：支持多种颜色输出模式（never/detect/always），提升日志可读性
- **属性注入**：支持自定义测试属性注入
- **早期配置**：使用 ApplicationListener 在应用启动早期阶段进行配置

### 工具函数

- **日志工具**：提供 Kotlin 风格的日志记录函数
- **Spring MVC 工具**：提供测试期间的 MVC 相关工具函数
- **Testcontainers 支持**：集成 Testcontainers 测试工具

## 使用方式

### 1. 添加依赖

在你的模块的 `build.gradle.kts` 中添加：

```kotlin
dependencies {
  testImplementation(project(":testtoolkit"))
}
```

### 2. 自动配置

TestToolkit 使用 Spring Boot 自动配置机制，无需手动配置即可使用。

#### 默认配置

```yaml
# application-test.yml
compose:
  testtoolkit:
    enabled: true                              # 启用测试工具包
    disable-condition-evaluation-report: true  # 关闭条件评估报告
    enable-virtual-threads: true               # 启用虚拟线程
    ansi-output-mode: always                   # ANSI 颜色输出模式
```

#### 自定义配置

```yaml
# application-test.yml
compose:
  testtoolkit:
    enabled: true
    disable-condition-evaluation-report: false
    enable-virtual-threads: true
    ansi-output-mode: detect                   # never, detect, always
    additional-properties:
      custom.property.1: "value1"
      custom.property.2: "value2"
```

### 3. 测试类示例

```kotlin
@SpringBootTest
class MyServiceTest {

  @Autowired
  private lateinit var myService: MyService

  @Test
  fun `测试服务功能`() {
    log.trace("testing service functionality")

    val result = myService.doSomething()

    assertTrue(result.isSuccess, "服务调用应该成功")

    log.debug("service test completed")
  }
}
```

### 4. 禁用自动配置

如果需要在特定测试中禁用自动配置：

```kotlin
@SpringBootTest
@TestPropertySource(properties = ["compose.testtoolkit.enabled=false"])
class MySpecialTest {
  // 测试代码
}
```

## 配置属性

| 属性名                                                       | 类型                  | 默认值    | 说明                               |
|-----------------------------------------------------------|---------------------|--------|----------------------------------|
| `compose.testtoolkit.enabled`                             | Boolean             | true   | 是否启用测试工具包                        |
| `compose.testtoolkit.disable-condition-evaluation-report` | Boolean             | true   | 是否关闭条件评估报告                       |
| `compose.testtoolkit.enable-virtual-threads`              | Boolean             | true   | 是否启用虚拟线程                         |
| `compose.testtoolkit.ansi-output-mode`                    | AnsiOutputMode      | always | ANSI 颜色输出模式（never/detect/always） |
| `compose.testtoolkit.additional-properties`               | Map<String, String> | {}     | 额外的测试属性                          |

## 提供的 Bean

- **TestConfigurationBean**：主要的配置 Bean
- **TestEnvironmentPostProcessor**：测试环境后处理器

## 工具函数

### 日志函数

```kotlin
// 获取日志实例
val log = this.log

// 直接打印变量值
log.info(::variableName)
```

### 类型别名

```kotlin
typealias SysLogger = org.slf4j.Logger
typealias RDBRollback = Rollback
typealias TempDirMapping = TempDir
```

## 最佳实践

1. **测试方法命名**：使用中文命名测试方法，描述测试场景
2. **日志记录**：在测试方法开始和结束时记录日志，便于调试
3. **属性配置**：将测试相关的配置放在 `application-test.yml` 中
4. **环境隔离**：使用 profile 区分不同的测试环境

## 注意事项

- 自动配置仅在测试环境中生效
- 颜色输出需要终端支持 ANSI 颜色
- 条件评估报告的关闭可能会影响调试，可根据需要开启 
