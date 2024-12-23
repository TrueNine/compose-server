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

  /**
   * 是否为内置类型？此表示该类型无需明确导入
   *
   * 例如：[kotlin.String] 和 java 的 [java.lang.Integer] ，再比如 typescript 中的 `string` `unknown` 等等
   */
  val builtin: Boolean?
}
