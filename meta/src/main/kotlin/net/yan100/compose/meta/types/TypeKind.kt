package net.yan100.compose.meta.types

enum class TypeKind {
  CLASS,
  INTERFACE,
  ENUM_CLASS,
  ENUM_ENTRY,
  ANNOTATION_CLASS,
  OBJECT,

  /** 类型别名 */
  TYPEALIAS,

  /** 瞬态类型，这表示该类型正在被处理，非稳定可存储类型 */
  TRANSIENT,

  /** jimmer 的 entity */
  IMMUTABLE,

  /** jimmer 的 复合属性 */
  EMBEDDABLE,
}
