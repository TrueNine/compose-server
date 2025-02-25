package net.yan100.compose.meta.client

sealed class ClientTypeName {
  /** 泛型名称 */
  data class GenericName(val typeName: String) : ClientTypeName()

  /** 普通名称 */
  data class QualifiedName(val typeName: String) : ClientTypeName()
}
