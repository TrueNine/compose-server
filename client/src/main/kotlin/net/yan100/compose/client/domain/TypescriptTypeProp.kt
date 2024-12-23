package net.yan100.compose.client.domain

sealed class TypescriptTypeProp(
  open val name: String,
  open val partial: Boolean = true,
  open val quota: TypescriptScopeQuota = TypescriptScopeQuota.BLANK
) {

  /**
   * 普通的左右值定义形式
   */
  data class Defined(
    override val name: String,
    val typeName: String,
    override val partial: Boolean = true,
    val usedGenerics: List<TypescriptUsedGeneric> = emptyList(),
    override val quota: TypescriptScopeQuota = TypescriptScopeQuota.BLANK,
  ) : TypescriptTypeProp(name, partial, quota)

  /**
   * 元组类型定义
   */
  data class Tuple(
    override val name: String,
    override val partial: Boolean = true,
    val elements: List<TypescriptTypeProp> = emptyList(),
  ) : TypescriptTypeProp(name, partial, TypescriptScopeQuota.ARRAY)

  /**
   * 嵌套对象
   */
  data class Object(
    override val name: String,
    override val partial: Boolean = true,
    val properties: List<TypescriptTypeProp> = emptyList(),
  ) : TypescriptTypeProp(name, partial, TypescriptScopeQuota.OBJECT)
}
