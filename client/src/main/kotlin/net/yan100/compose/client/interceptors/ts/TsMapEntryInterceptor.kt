package net.yan100.compose.client.interceptors.ts

import net.yan100.compose.client.contexts.KtToTsContext
import net.yan100.compose.client.domain.TsGeneric
import net.yan100.compose.client.domain.TsTypeVal
import net.yan100.compose.client.domain.TsUseVal
import net.yan100.compose.client.interceptors.TsPostReferenceInterceptor
import net.yan100.compose.client.toTsStyleName
import net.yan100.compose.meta.client.ClientType

class TsMapEntryInterceptor : TsPostReferenceInterceptor() {
  private val nameMap = listOf(
    "java.util.Map\$Entry",
    "kotlin.collections.Map\$Entry",
  ).associateWith {
    TsTypeVal.Object(
      elements = listOf(
        TsUseVal.Prop(
          name = "key".toTsStyleName(),
          typeVal = TsTypeVal.Generic(TsGeneric.UnUsed)
        ),
        TsUseVal.Prop(
          name = "value".toTsStyleName(),
          typeVal = TsTypeVal.Generic(TsGeneric.UnUsed)
        )
      )
    )
  }

  override fun supported(ctx: KtToTsContext, source: ClientType): Boolean = source.typeName in nameMap
  override fun process(ctx: KtToTsContext, source: ClientType): TsTypeVal<*> = nameMap[source.typeName]!!
}
