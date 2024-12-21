package net.yan100.compose.meta.types

interface TypeName {
  /**
   * 类名
   */
  val typeName: String

  /**
   * 注释
   */
  val doc: Doc?

  /**
   * 类型的分类
   */
  val typeKind: TypeKind?

  /**
   * 该类继承的父类列表
   */
  val superTypes: List<TypeName>
}
