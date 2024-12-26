package net.yan100.compose.client.interceptors.kt

import net.yan100.compose.client.contexts.KtToKtContext
import net.yan100.compose.client.interceptors.Interceptor
import net.yan100.compose.client.interceptors.KotlinToKotlinInterceptor
import net.yan100.compose.meta.client.ClientType
import net.yan100.compose.meta.types.TypeKind


open class JavaInternalInterceptor : KotlinToKotlinInterceptor() {
  override val executeStage: Interceptor.ExecuteStage = Interceptor.ExecuteStage.BEFORE_ALWAYS
  override fun defaultProcess(ctx: KtToKtContext, source: ClientType): ClientType = source
  val supportedKinds = listOf(
    TypeKind.CLASS,
    TypeKind.INTERFACE,
  )

  override fun supported(ctx: KtToKtContext, source: ClientType): Boolean {
    return source.typeKind in supportedKinds
  }

  override fun process(ctx: KtToKtContext, source: ClientType): ClientType {
    return source
  }
}
