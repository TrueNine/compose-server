package io.github.truenine.composeserver.rds.enums

import io.github.truenine.composeserver.IIntEnum
import org.babyfish.jimmer.sql.EnumItem
import org.babyfish.jimmer.sql.EnumType

/** Certificate surface type */
@EnumType(EnumType.Strategy.ORDINAL)
enum class CertPointTyping(private val v: Int) : IIntEnum {
  /** No requirement */
  @EnumItem(ordinal = 0) NONE(0),

  /** Front side */
  @EnumItem(ordinal = 1) HEADS(1),

  /** Back side */
  @EnumItem(ordinal = 2) TAILS(2),

  /** Double-sided */
  @EnumItem(ordinal = 3) DOUBLE(3),

  /** All */
  @EnumItem(ordinal = 4) ALL(4),

  /** All content */
  @EnumItem(ordinal = 5) ALL_CONTENT(5),

  /**
   * Complete
   *
   * Used for video, audio and similar content.
   */
  @EnumItem(ordinal = 6) INTACT(6);

  override val value: Int = v

  companion object {
    @JvmStatic fun findVal(value: Int?) = entries.find { value == it.v }

    @JvmStatic operator fun get(v: Int?) = findVal(v)
  }
}
