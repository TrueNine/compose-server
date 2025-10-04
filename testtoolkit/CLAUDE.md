# Test Toolkit

Test toolkit providing automatic configuration and utility functions during testing.

## Features

### Auto Configuration

- **Disable Condition Evaluation Report**: Automatically disable Spring Boot's condition evaluation report to reduce test log noise
- **Disable Banner**: Automatically disable Spring Boot startup banner to simplify test output
- **Enable Virtual Threads**: Enable JDK virtual threads by default to improve test performance
- **ANSI Color Output**: Support multiple color output modes (never/detect/always) to enhance log readability
- **Property Injection**: Support custom test property injection
- **Early Configuration**: Use ApplicationListener for configuration in early application startup stages

### Utility Functions

- **Logging Utilities**: Provide Kotlin-style logging functions
- **Spring MVC Utilities**: Provide MVC-related utility functions during testing
- **Testcontainers Support**: Integrate Testcontainers testing tools

## Usage

### 1. Add Dependencies

Add to your module's `build.gradle.kts`:

```kotlin
dependencies {
  testImplementation(project(":testtoolkit"))
}
```

### 2. Auto Configuration

TestToolkit uses Spring Boot's auto-configuration mechanism, no manual configuration required.

#### Default Configuration

```yaml
# application-test.yml
compose:
  testtoolkit:
    enabled: true                              # Enable test toolkit
    disable-condition-evaluation-report: true  # Disable condition evaluation report
    enable-virtual-threads: true               # Enable virtual threads
    ansi-output-mode: always                   # ANSI color output mode
```

#### Custom Configuration

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

### 3. Test Class Example

```kotlin
@SpringBootTest
class MyServiceTest {

  @Autowired
  private lateinit var myService: MyService

  @Test
  fun `test service functionality`() {
    log.trace("testing service functionality")

    val result = myService.doSomething()

    assertTrue(result.isSuccess, "service call should succeed")

    log.debug("service test completed")
  }
}
```

### 4. Disable Auto Configuration

To disable auto configuration in specific tests:

```kotlin
@SpringBootTest
@TestPropertySource(properties = ["compose.testtoolkit.enabled=false"])
class MySpecialTest {
  // Test code
}
```

## Configuration Properties

| Property Name                                               | Type                  | Default | Description                                   |
|-----------------------------------------------------------|---------------------|--------|--------------------------------------------|
| `compose.testtoolkit.enabled`                             | Boolean             | true   | Whether to enable test toolkit               |
| `compose.testtoolkit.disable-condition-evaluation-report` | Boolean             | true   | Whether to disable condition evaluation report |
| `compose.testtoolkit.enable-virtual-threads`              | Boolean             | true   | Whether to enable virtual threads            |
| `compose.testtoolkit.ansi-output-mode`                    | AnsiOutputMode      | always | ANSI color output mode (never/detect/always) |
| `compose.testtoolkit.additional-properties`               | Map<String, String> | {}     | Additional test properties                   |

## Provided Beans

- **TestConfigurationBean**: Main configuration bean
- **TestEnvironmentPostProcessor**: Test environment post-processor

## Utility Functions

### Logging Functions

```kotlin
// Get logger instance
val log = this.log

// Print variable values directly
log.info(::variableName)
```

### Type Aliases

```kotlin
typealias SysLogger = org.slf4j.Logger
typealias RDBRollback = Rollback
typealias TempDirMapping = TempDir
```

## Best Practices

1. **Test Method Naming**: Use Chinese naming for test methods to describe test scenarios
2. **Logging**: Log at the beginning and end of test methods for debugging
3. **Property Configuration**: Place test-related configurations in `application-test.yml`
4. **Environment Isolation**: Use profiles to distinguish different test environments

## Notes

- Auto configuration only takes effect in test environments
- Color output requires terminal ANSI color support
- Disabling condition evaluation report may affect debugging, enable as needed