package net.yan100.compose.client.contexts

import net.yan100.compose.meta.client.ClientType
import kotlin.reflect.KClass

@Deprecated("仅用于测试使用")
internal class NoneContext : StubContext<NoneContext>() {
  override fun getAllTypes(): List<ClientType> = TODO("Not yet implemented")
  override fun getTypeByName(typeName: String): ClientType = TODO("Not yet implemented")
  override fun getTypeByKClass(kClass: KClass<*>): ClientType = TODO("Not yet implemented")
  override fun addType(type: ClientType): NoneContext = TODO("Not yet implemented")
  override var currentStage: ExecuteStage = ExecuteStage.RESOLVE_OPERATIONS
}
