package net.yan100.compose.client.interceptors.ts

import net.yan100.compose.client.contexts.KtToTsContext
import net.yan100.compose.client.domain.TsTypeVal
import net.yan100.compose.client.interceptors.TsPreReferenceInterceptor
import net.yan100.compose.client.toTsGenericUsed
import net.yan100.compose.client.toTsPathName
import net.yan100.compose.client.unwrapGenericName
import net.yan100.compose.meta.client.ClientType

class TsTypeValPreReferenceInterceptor : TsPreReferenceInterceptor() {
  override fun supported(ctx: KtToTsContext, source: ClientType): Boolean = true

  override fun process(ctx: KtToTsContext, source: ClientType): TsTypeVal<*> {
    return TsTypeVal.Ref(
      typeName = ctx.getTypeNameByName(source.typeName).toTsPathName(),
      usedGenerics =
        source.toTsGenericUsed {
          ctx
            .getTypeNameByName(source.typeName)
            .unwrapGenericName()
            .toTsPathName()
        },
    )
  }
}
