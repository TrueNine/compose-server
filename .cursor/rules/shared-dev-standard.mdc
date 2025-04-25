---
description: 强制编码守则
globs:
alwaysApply: false
---

## 强制：避免无谓对象创建

```java
String s = "a";
for (int i = 0; i < n; i++) {
  // 用 s
}
```

## 强制：禁止非空断言，优先判空或安全调用

```kotlin
if (user == null) return
println(user.name)
```

## 强制：条件/循环/switch等必须加大括号

```c
if (x) {
  do();
}
```

## 强制：早返回，减少嵌套

```kotlin
// 不推荐
if (a != null) { 
  if (a.b != null) {
    // ...
  }
}
// 推荐
if (a == null) {
  return
}
if (a.b == null) {
  return
}
```

## 强制：优化循环与递归

避免重复计算，支持尾递归

## 强制：尽量使用函数式编程风格

- 推荐使用 map、filter、reduce、forEach 等函数式 API 简化遍历、转换、聚合等操作，提升代码可读性与表达力。
- Java 推荐 stream API，TypeScript/JavaScript 推荐数组的高阶函数。
- 能用函数式表达的场景，尽量避免传统 for/while 循环和冗余变量。

```java
// 不推荐
List<String> result = new ArrayList<>();
for (User u : users) {
  if (u.isActive()) {
    result.add(u.getName());
  }
}
// 推荐
List<String> result = users.stream()
  .filter(User::isActive)
  .map(User::getName)
  .collect(Collectors.toList());
```

```typescript
// 不推荐
const names: string[] = [];
for (const u of users) {
  if (u.active) {
    names.push(u.name)
  }
}
// 推荐
const names = users.filter(u => u.active).map(u => u.name)
```

## 推荐：及时释放资源

```go
defer f.Close()
```

## 推荐：避免反射和动态调用

直接方法调用优于反射

## 推荐：减少类型转换和装箱

```csharp
int sum = 0;
for (int i = 0; i < 1000; i++) 
{
  sum += i;
}
```

## 推荐：优先用不可变数据结构

```kotlin
val name = "Alice"
```

## 推荐：注释规范
- 业务注释用中文，库代码用英文
- 注释放在代码上方，禁止行尾注释

## 推荐：用查表替代多分支

```kotlin
val map = mapOf(1 to "A", 2 to "B")
fun getLabel(v: Int) = map[v]
```

## 推荐：延迟初始化

按需加载资源或配置，避免无谓消耗。

```go
var config *Config
func GetConfig() *Config {
  if config == nil {
    config = loadConfig()
  }
  return config
}
```

## 可选：分层解耦

单一职责，接口隔离，模块化

## 可选：静态分析与自动化测试

保持高测试覆盖率，使用分析工具

## 可选：锁粒度小，避免死锁

只锁关键区

## 可选：利用编译器优化

`inline`、`final`、`const`等关键字
