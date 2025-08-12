---
inclusion: fileMatch
fileMatchPattern: "**/*.(kt|gradle|kts)"
---

**Kotlin 规则**

1. 优先使用val声明不可变变量
2. 避免!!操作符，使用?.或let{}
3. 数据类替代多参数函数
4. 严禁使用 `println` 记录输出
5. 严禁在单元测试中使用 `mockito`，而是使用 `mockk`
6. 扩展方法与纯函数应分开，不应在同一文件中
  + 扩展函数文件命名规范：使用 `*Extensions.kt` 后缀
  + 纯函数文件命名规范：使用 `*Functions.kt` 后缀
