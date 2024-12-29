package net.yan100.compose.client.contexts

/**
 * ### 拦截器的触发时机
 *
 * 大类分为以下三个阶段
 * - before 在处理存根文件
 * - link 将 kotlin 处理为 typescript
 * - after 将 typescript 处理为 typescript
 *
 * 小分类分为
 *
 * - pre_process 预处理
 * - post_process 后处理
 * - always 无论何时总是触发
 */
enum class ExecuteStage {
  /**
   * 提供类名称
   */
  CONVERT_QUALIFIER_NAME,

  /**
   * 循环替换 class
   */
  LOOP_RESOLVE_CLASS,

  /**
   * 提供类型引用
   */
  LOOP_RESOLVE_TS_REFERENCES,

  /**
   * 此阶段提供基本的 ts scope 声明
   *
   * - 枚举 被直接提供
   * - 接口 被直接提供（但未填写泛型参数）
   * - 别名 被直接提供（但未填写泛型）
   */
  RESOLVE_TS_SCOPE,

  /**
   * 此阶段循环提供 ts scope 的所有属性
   * - 接口 （已填写泛型参数）
   * - 别名 （已填写泛型）
   */
  RESOLVE_TS_SCOPE_ALWAYS,

  RESOLVE_SERVICE,
  RESOLVE_OPERATIONS
}
