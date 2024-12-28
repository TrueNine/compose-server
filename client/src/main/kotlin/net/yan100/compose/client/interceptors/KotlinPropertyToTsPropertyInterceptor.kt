package net.yan100.compose.client.interceptors

import net.yan100.compose.client.contexts.ExecuteStage
import net.yan100.compose.client.contexts.KtToTsContext
import net.yan100.compose.client.domain.TsTypeProperty
import net.yan100.compose.client.domain.TsTypeVal
import net.yan100.compose.client.toTsStylePathName
import net.yan100.compose.meta.client.ClientProp

abstract class KotlinPropertyToTsPropertyInterceptor : Interceptor<ClientProp, TsTypeProperty, KtToTsContext> {
  override val executeStage: ExecuteStage = ExecuteStage.LOOP_RESOLVE_CLASS
  override fun defaultProcess(ctx: KtToTsContext, source: ClientProp): TsTypeProperty =
    TsTypeProperty(source.typeName.toTsStylePathName(), defined = TsTypeVal.Unknown)
}
