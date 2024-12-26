package net.yan100.compose.client.contexts

import net.yan100.compose.client.interceptors.Interceptor
import net.yan100.compose.meta.client.ClientType
import kotlin.reflect.KClass

abstract class StubContext<C : StubContext<C>>(
  protected val interceptorChain: MutableList<Interceptor<*, *, *>> = mutableListOf()
) {
  inline fun <S : Any, T : Any, reified I : Interceptor<S, T, C>> List<I>.processOrNull(
    ctx: C,
    definition: S
  ): T? {
    return filterIsInstance<I>().firstOrNull { interceptor ->
      interceptor.supported(ctx, definition)
    }?.process(ctx, definition)
  }


  @Suppress("UNCHECKED_CAST")
  fun addInterceptor(interceptor: Interceptor<*, *, *>): C {
    interceptorChain.add(interceptor)
    return this as C
  }

  fun getInterceptors(): List<Interceptor<*, *, *>> {
    return interceptorChain
  }

  @Suppress("UNCHECKED_CAST")
  fun addInterceptors(interceptors: Iterable<Interceptor<*, *, *>>): C {
    interceptorChain += interceptors
    return this as C
  }

  abstract fun getAllClientTypes(): List<ClientType>
  abstract fun getClientTypeByQualifierName(qualifierName: String): ClientType?
  abstract fun getClientTypeByKClass(kClass: KClass<*>): ClientType?

  protected abstract fun addClientType(type: ClientType): C

  abstract var currentStage: Interceptor.ExecuteStage
    protected set
}
