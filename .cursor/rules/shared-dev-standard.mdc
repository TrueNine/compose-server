---
description: 强制编码守则
globs:
alwaysApply: false
---

# 强制编码守则

## 强制规则
- **保证代码可测试性**：依赖必须可注入，逻辑与IO分离，副作用可控
```java
// ✓ class Service(private val repository: Repository) { ... }
// ✗ class Service { private val repository = Repository() }
```
- **避免无谓对象创建**：`String s = "a"; for (int i = 0; i < n; i++) { /* 用 s */ }`
- **禁止非空断言**：优先判空或安全调用 `if (user == null) return; println(user.name)`
- **条件/循环必加大括号**：`if (x) { do(); }`
- **早返回减少嵌套**：
```kotlin
// ✓ if (a == null) return; if (a.b == null) return;
// ✗ if (a != null) { if (a.b != null) { ... } }
```
- **函数式编程风格**：
```ts
// ✓ users.filter(u => u.active).map(u => u.name)
// ✗ 传统循环+变量收集
```

## 推荐规则

- **及时释放资源**：`defer f.Close()`
- **避免反射和动态调用**：直接方法调用优于反射
- **减少类型转换和装箱**：避免隐式转换
- **优先不可变数据**：`val name = "Alice"`
- **注释规范**：业务中文/库英文，上方注释，禁行尾
- **查表替代多分支**：`val map = mapOf(1 to "A"); fun get(v) = map[v]`
- **延迟初始化**：按需加载资源

## 可选建议

- **分层解耦**：单一职责，接口隔离，模块化
- **自动化测试**：高测试覆盖率，使用静态分析
- **小锁粒度**：只锁关键区，避免死锁
- **利用编译优化**：`inline`、`final`、`const`等关键字
