package io.github.truenine.composeserver.ksp.meta.types

enum class TypeKind {
  CLASS,
  INTERFACE,
  ENUM_CLASS,
  ENUM_ENTRY,
  ANNOTATION_CLASS,
  OBJECT,

  /** Type alias */
  TYPEALIAS,

  /** Transient type, meaning this type is being processed and is not a stable storable type */
  TRANSIENT,

  /** Jimmer entity */
  IMMUTABLE,

  /** Jimmer embeddable (composite property) */
  EMBEDDABLE,
}
