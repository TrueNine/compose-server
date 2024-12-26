package net.yan100.compose.client.contexts

import net.yan100.compose.client.interceptors.Interceptor
import net.yan100.compose.meta.client.ClientType
import kotlin.reflect.KClass

@Deprecated("仅用于测试使用")
internal class NoneContext : StubContext<NoneContext>() {
  override fun getAllClientTypes(): List<ClientType> = TODO("Not yet implemented")
  override fun getClientTypeByQualifierName(qualifierName: String): ClientType = TODO("Not yet implemented")
  override fun getClientTypeByKClass(kClass: KClass<*>): ClientType = TODO("Not yet implemented")
  override fun addClientType(type: ClientType): NoneContext = TODO("Not yet implemented")
  override var currentStage: Interceptor.ExecuteStage = Interceptor.ExecuteStage.AFTER_ALWAYS
}
