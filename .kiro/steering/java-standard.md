---
inclusion: fileMatch
fileMatchPattern: "**/*.java"
---

**Java 规则**

所有JVM平台的编程语言：java、kotlin、groovy、scala 均需遵守以下原则

1. 可使用 `import xxx.*` 导入来减少代码量，后续交由IDE进行处理
2. 尽可能使用jdk的新特性
3. 声明变量应尽量使用 `final var`
4. 积极使用 lambda
5. 严禁使用 `System.out.println` 记录输出
