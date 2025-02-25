package net.yan100.compose.client.interceptors.ts

import net.yan100.compose.client.contexts.KtToTsContext
import net.yan100.compose.client.domain.TsGeneric
import net.yan100.compose.client.domain.TsTypeVal
import net.yan100.compose.client.interceptors.TsPostReferenceInterceptor
import net.yan100.compose.meta.client.ClientType

open class TsListTypeValInterceptor : TsPostReferenceInterceptor() {
  private val nameMap =
    listOf(
        "java.util.Iterable",
        "java.util.Collection",
        "java.lang.List",
        "java.util.Set",
      )
      .associateWith { TsTypeVal.Array(usedGeneric = TsGeneric.UnUsed) }

  override fun supported(ctx: KtToTsContext, source: ClientType): Boolean {
    return nameMap.containsKey(source.typeName) ||
      nameMap.any { source.isAssignableFrom(it.key) }
  }

  override fun process(ctx: KtToTsContext, source: ClientType): TsTypeVal<*> {
    return TsTypeVal.Array(TsGeneric.UnUsed)
  }
}
