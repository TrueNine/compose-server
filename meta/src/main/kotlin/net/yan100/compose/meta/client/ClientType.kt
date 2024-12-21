package net.yan100.compose.meta.client

import net.yan100.compose.meta.types.Doc
import net.yan100.compose.meta.types.TypeKind
import net.yan100.compose.meta.types.TypeName

/**
 * 类型定义
 */
data class ClientType(
  override val typeName: String,
  override val doc: Doc? = null,
  override val typeKind: TypeKind? = null,
  override val superTypes: List<ClientType> = emptyList(),

  /**
   * 是否为别名
   */
  val isAlias: Boolean? = null,

  /**
   * 该别名对应的类型名
   */
  val aliasForTypeName: String? = null,

  /**
   * 该类的泛型列表，仅表示当前类需要填写多少泛型参数
   */
  val argumentLocations: List<String> = emptyList(),

  /**
   * 所填写的泛型参数
   */
  val inputGenerics: List<ClientInputGenericType> = emptyList(),

  /**
   * 该类具有的属性列表
   */
  val properties: List<ClientProp> = emptyList(),


  /**
   * 枚举常量值列表
   *
   * 如果这是一个枚举类型
   */
  val enumConstants: Map<String, Int> = emptyMap(),

  /**
   * 是否建议为 null
   */
  val nullable: Boolean? = null,
) : TypeName
