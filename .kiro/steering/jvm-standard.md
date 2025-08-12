---
inclusion: fileMatch
fileMatchPattern: "**/*.(kt|java|gradle|kts|groovy)"
---

**JVM Rules**

1. Strictly prohibit using `@DisplayName` annotation in test code
2. In spring/quarkus, strictly prohibit using framework-specific annotations, for example: `@Autowired` must be replaced with `@Resource`
3. Use the maximum extent of new features available in the project's JDK version as much as possible
4. Prohibit using `@DisplayName` annotation in unit tests
5. Unit test method names should use English naming with underscore separation
6. Prohibit unit test method names from starting with `test_` or `should_`
7. When logging, use the format `log.info("message param1: {}, param2{}")`
8. Prohibit logging detailed sensitive information, such as: passwords, ID numbers, API keys, phone numbers, etc.
