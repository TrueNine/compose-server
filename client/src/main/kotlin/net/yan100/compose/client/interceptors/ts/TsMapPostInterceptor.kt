package net.yan100.compose.client.interceptors.ts

import net.yan100.compose.client.contexts.KtToTsContext
import net.yan100.compose.client.domain.TsGeneric
import net.yan100.compose.client.domain.TsTypeVal
import net.yan100.compose.client.interceptors.TsPostReferenceInterceptor
import net.yan100.compose.meta.client.ClientType

class TsMapPostInterceptor : TsPostReferenceInterceptor() {
  private val nameMap = listOf(
    "kotlin.collections.Map",
    "java.util.Map"
  ).associateWith {
    TsTypeVal.Record(
      keyUsedGeneric = TsGeneric.Used(TsTypeVal.String),
      valueUsedGeneric = TsGeneric.Used(TsTypeVal.Unknown)
    )
  }

  override fun supported(ctx: KtToTsContext, source: ClientType): Boolean = source.typeName in nameMap
  override fun process(ctx: KtToTsContext, source: ClientType): TsTypeVal<*> = nameMap[source.typeName]!!
}
