package net.yan100.compose.client.interceptors

import net.yan100.compose.client.contexts.ExecuteStage
import net.yan100.compose.client.contexts.KtToKtContext
import net.yan100.compose.meta.client.ClientInputGenericType

class KotlinUsedGenericInterceptor : Interceptor<ClientInputGenericType, ClientInputGenericType, KtToKtContext> {
  override val executeStage: ExecuteStage = ExecuteStage.CONVERT_QUALIFIER_NAME

  override fun supported(ctx: KtToKtContext, source: ClientInputGenericType): Boolean {
    TODO("Not yet implemented")
  }

  override fun process(ctx: KtToKtContext, source: ClientInputGenericType): ClientInputGenericType {
    TODO("Not yet implemented")
  }

  override fun defaultProcess(ctx: KtToKtContext, source: ClientInputGenericType): ClientInputGenericType {
    TODO("Not yet implemented")
  }
}
