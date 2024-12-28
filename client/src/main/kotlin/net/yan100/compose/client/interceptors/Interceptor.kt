package net.yan100.compose.client.interceptors

import net.yan100.compose.client.contexts.ExecuteStage
import net.yan100.compose.client.contexts.StubContext


interface Interceptor<S : Any, T : Any, C : StubContext<*>> {
  /**
   * 拦截器的触发时机
   * @see ExecuteStage
   */
  val executeStage: ExecuteStage

  fun supported(ctx: C, source: S): Boolean
  fun process(ctx: C, source: S): T
  fun defaultProcess(ctx: C, source: S): T
  val interceptorName: String get() = this::class.simpleName ?: ""
}
