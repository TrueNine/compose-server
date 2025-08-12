---
inclusion: fileMatch
fileMatchPattern: "**/*.(kt|java|gradle|kts|groovy)"
---

**JVM 规则**

1. 严禁在测试代码中使用 `@DisplayName` 注解
2. spring/quarkus 中严禁使用特定框架的注解，例如：`@Autowired`必须使用 `@Resource` 替代
3. 尽可能使用项目内JDK版本能使用的最大限度的新特性
4. 禁止在单元测试中使用 `@DisplayName` 注解
5. 单元测试方法名称使用英文命名，以及下划线分割
6. 禁止单元测试方法名以 `test_`、`should_` 开头
7. 记录日志时，格式为 `log.info("message param1: {}, param2{}")` 这种格式
8. 禁止记录详细的敏感信息，例如：密码、身份证号、APIKEY、手机号等
