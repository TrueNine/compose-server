package net.yan100.compose.meta.client

import net.yan100.compose.meta.types.Doc

/** 类其下的属性 */
data class ClientProp(
  /** 属性名称 */
  val name: String,

  /** 属性类型 */
  val typeName: String,

  /** 属性注释 */
  val doc: Doc? = null,

  /** 填写的泛型参数列表 */
  val usedGenerics: List<ClientUsedGeneric> = emptyList(),

  /** 属性索引 */
  val index: Int? = null,

  /** 是否可空 */
  val nullable: Boolean? = false,

  /** 是否定义了默认值 */
  val definedDefaultValue: Boolean? = null,
) {
  fun resolveAll() {}
}
