package net.yan100.compose.client.contexts

import net.yan100.compose.client.interceptors.Interceptor
import net.yan100.compose.client.interceptors.kt.KotlinQualifierNameInterceptor
import net.yan100.compose.meta.client.ClientApiStubs
import net.yan100.compose.meta.client.ClientType
import kotlin.reflect.KClass

open class KtToKtContext(
  stub: ClientApiStubs,
  vararg interceptorChain: Interceptor<*, *, *> = arrayOf()
) : StubContext<KtToKtContext>(interceptorChain.toMutableList()) {
  private val stub = stub.copy()

  private val internalDefinitions: MutableList<ClientType> = mutableListOf()
  val definitions: List<ClientType>
    get() {
      if (internalDefinitions.isEmpty()) internalDefinitions += getProcessedDefinitions()
      return internalDefinitions
    }

  private fun getProcessedDefinitions(): MutableList<ClientType> {
    val convertedName = stub.definitions.map {
      it.copy(
        typeName = resolveClientTypeNameByName(it.typeName),
        superTypes = convertSuperTypes(it),
        aliasForTypeName = it.aliasForTypeName?.let { n -> resolveClientTypeNameByName(n) }
      )
    }
    return convertedName.toMutableList()
  }


  private fun convertSuperTypes(clientType: ClientType): List<ClientType> {
    return if (clientType.superTypes.isEmpty()) emptyList()
    else {
      clientType.superTypes.map {
        it.copy(
          typeName = resolveClientTypeNameByName(it.typeName),
          superTypes = convertSuperTypes(it)
        )
      }
    }
  }

  fun resolveClientTypeNameByName(
    name: String
  ): String {
    val chains = interceptorChain.filterIsInstance<KotlinQualifierNameInterceptor>()
    if (chains.isEmpty()) error("Not a single name interceptor was found, which may not have been expected")
    return chains.processOrNull(this, name) ?: name
  }


  private val definitionsMap: Map<String, ClientType> get() = internalDefinitions.associateBy { it.typeName }


  override fun getAllClientTypes(): List<ClientType> = internalDefinitions
  override fun getClientTypeByQualifierName(qualifierName: String): ClientType? = definitionsMap[qualifierName]
  override fun getClientTypeByKClass(kClass: KClass<*>): ClientType? = getClientTypeByQualifierName(kClass.simpleName!!)

  override fun addClientType(type: ClientType): KtToKtContext {
    if (definitionsMap.containsKey(type.typeName)) internalDefinitions.removeIf { it.typeName == type.typeName }
    internalDefinitions += type
    return this
  }

  override var currentStage: Interceptor.ExecuteStage = Interceptor.ExecuteStage.BEFORE_ALWAYS
}
