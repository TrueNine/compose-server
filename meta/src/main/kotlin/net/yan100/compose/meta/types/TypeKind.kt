package net.yan100.compose.meta.types

enum class TypeKind {
  CLASS, INTERFACE, ENUM_CLASS, ENUM_ENTRY, ANNOTATION_CLASS, OBJECT,

  /**
   * 类型别名
   */
  TYPEALIAS,

  /**
   * jimmer 的 entity
   */
  IMMUTABLE,

  /**
   * jimmer 的 复合属性
   */
  EMBEDDABLE;
}
