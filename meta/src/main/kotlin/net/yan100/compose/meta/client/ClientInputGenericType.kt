package net.yan100.compose.meta.client

/**
 * 类泛型定义
 */
data class ClientInputGenericType(
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
  val inputGenerics: List<ClientInputGenericType> = emptyList()
) {
  fun changeAllToCopy(
    newInputGenericResolver: (ClientInputGenericType) -> ClientInputGenericType?
  ): ClientInputGenericType? {
    val r = newInputGenericResolver(this) ?: return null
    return r.copy(inputGenerics = resolveAllSuperTypes(r.inputGenerics, newInputGenericResolver))
  }

  private fun resolveAllSuperTypes(
    inputGenerics: List<ClientInputGenericType>,
    newInputGenericResolver: (ClientInputGenericType) -> ClientInputGenericType?
  ): List<ClientInputGenericType> {
    return inputGenerics.mapNotNull {
      val r = newInputGenericResolver(it)
      r?.copy(inputGenerics = resolveAllSuperTypes(it.inputGenerics, newInputGenericResolver))
    }
  }
}
