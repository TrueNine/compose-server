package net.yan100.compose.meta.client

/**
 * 类泛型定义
 */
data class ClientUsedGeneric(
  /**
   * 需填写的类型
   */
  val typeName: String,
  /**
   * 在参数列表中的位置
   */
  val index: Int,
  /**
   * 是否可空
   */
  val nullable: Boolean? = null,
  /**
   * 套娃泛型参数
   */
  val usedGenerics: List<ClientUsedGeneric> = emptyList()
) {
  fun changeAllToCopy(
    newInputGenericResolver: (ClientUsedGeneric) -> ClientUsedGeneric?
  ): ClientUsedGeneric? {
    val r = newInputGenericResolver(this) ?: return null
    return r.copy(usedGenerics = resolveAllSuperTypes(r.usedGenerics, newInputGenericResolver))
  }

  private fun resolveAllSuperTypes(
    inputGenerics: List<ClientUsedGeneric>,
    newInputGenericResolver: (ClientUsedGeneric) -> ClientUsedGeneric?
  ): List<ClientUsedGeneric> {
    return inputGenerics.mapNotNull {
      val r = newInputGenericResolver(it)
      r?.copy(usedGenerics = resolveAllSuperTypes(it.usedGenerics, newInputGenericResolver))
    }
  }
}
