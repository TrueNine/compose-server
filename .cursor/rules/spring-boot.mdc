---
description: 
globs: *Api.kt,*Service.kt,*Repository.kt,*Controller.kt,*Config.kt
alwaysApply: false
---
- spring-boot3
- jakarta-ee

### 开发实践

- 依赖注入：构造器注入优先，字段注入使用`@Resource`+lateinit
- 异常处理：禁用 throw，强制使用`error`/`require`/`check`系列
- 实体主键类型为`id: RefId`（Long别名）,inputDto 或者 outputDto 中, jimmer 会将其转换为 `String`
- 贫血模型：Controller/Api 仅做参数校验和结果返回

---

- `RefId` 通常指代 `net.yan100.compose.core.RefId`, 它是 kotlin 的 typealias, 真实类型为 `kotlin.Long`
- `RefId` 为其准备了基本类型到 `RefId` 的扩展方法 `net.yan100.compose.core.toId`. 调用示例: `1l.toId()`,`"1".toId()`
- `RefId` 为其准备判断是否为`RefId`的扩展方法 `net.yan100.compose.core.isId`. 调用示例: `id.isId()`

---

- 后缀规则：Controller 类`Api`结尾，API路径含版本号（如`v1/user_account`）
- 分层命名：
  - Repository：`find`/`insert`/`update`/`delete`
  - Service：`fetch`/`post`/`remove`/`persist`
  - API：`get`/`post`/`put`/`delete`
- 禁用词：`ID`（统一使用`id`/`Id`）

- 命名规范：
  - 避免使用全大写缩写（如：使用`userId`而不是`userID`）
  - 避免使用后缀命名（如：使用`User`而不是`UserApi`）
  - 实体标识符统一使用`id`小写形式

```kotlin
/**
 * # 用户服务
 * > 提供用户相关的核心功能
 *
 * 处理用户注册、认证等基础服务
 * @param name 用户名称
 * @param email 电子邮箱
 */
class User {
  /**
   * ## 获取用户信息
   * @param userId 用户标识符
   * @return 用户详细信息
   */
  fun get(userId: RefId) {
    // ...
  }
}
```
