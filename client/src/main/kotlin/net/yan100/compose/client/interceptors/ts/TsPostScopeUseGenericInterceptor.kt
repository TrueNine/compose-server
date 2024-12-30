package net.yan100.compose.client.interceptors.ts

import net.yan100.compose.client.contexts.KtToTsContext
import net.yan100.compose.client.domain.TsScope
import net.yan100.compose.client.ifNotGenericName
import net.yan100.compose.client.interceptors.TsPostScopeInterceptor
import net.yan100.compose.meta.client.ClientType

class TsPostScopeUseGenericInterceptor : TsPostScopeInterceptor() {
  override fun supported(ctx: KtToTsContext, source: Pair<ClientType, TsScope<*>>): Boolean = source.second.isRequireUseGeneric()
  override fun process(ctx: KtToTsContext, source: Pair<ClientType, TsScope<*>>): TsScope<*> {
    val (typed, scope) = source
    val usedGenerics = ctx.getTsGenericByGenerics(typed.usedGenerics)
    TODO("Not yet implemented")
  }
}
