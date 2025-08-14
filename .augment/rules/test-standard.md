---
type: "agent_requested"
description: "Example description"
---
**Test Organization Best Practices:**

- Create main groupings for each tested class/function/variable/method
- Subdivide by scenarios: normal cases, exception cases, boundary cases
  + Kotlin example: `@Nested inner class CreateUser { @Test fun should_create_successfully() {} }`
