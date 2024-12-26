package net.yan100.compose.client.interceptors

import net.yan100.compose.client.contexts.StubContext


interface Interceptor<S : Any, T : Any, C : StubContext<*>> {
  /**
   * 拦截器的触发时机
   * @see ExecuteStage
   */
  val executeStage: ExecuteStage

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
    BEFORE_PRE_PROCESS,
    BEFORE_POST_PROCESS,
    BEFORE_ALWAYS,

    LINK_PRE_PROCESS,
    LINK_POST_PROCESS,
    LINK_ALWAYS,

    AFTER_PRE_PROCESS,
    AFTER_POST_PROCESS,
    AFTER_ALWAYS
  }


  fun supported(ctx: C, source: S): Boolean
  fun process(ctx: C, source: S): T
  fun defaultProcess(ctx: C, source: S): T
  val interceptorName: String get() = this::class.simpleName ?: ""
}
