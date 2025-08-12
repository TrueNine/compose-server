---
inclusion: fileMatch
fileMatchPattern: "**/*Test.(kt|java)"
---

**测试组织最佳实践：**

- 每个被测试类/函数/变量/方法创建主要分组
- 按场景细分：正常用例、异常用例、边界用例
  + 示例kotlin：`@Nested inner class CreateUser { @Test fun should_create_successfully() {} }`
