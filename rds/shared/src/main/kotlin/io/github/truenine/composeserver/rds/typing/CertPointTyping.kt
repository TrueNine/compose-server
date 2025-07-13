package io.github.truenine.composeserver.rds.typing

import io.github.truenine.composeserver.IIntTyping
import org.babyfish.jimmer.sql.EnumItem
import org.babyfish.jimmer.sql.EnumType

/** 证件印面类型 */
@EnumType(EnumType.Strategy.ORDINAL)
enum class CertPointTyping(private val v: Int) : IIntTyping {
  /** 无要求 */
  @EnumItem(ordinal = 0) NONE(0),

  /** 正面 */
  @EnumItem(ordinal = 1) HEADS(1),

  /** 反面 */
  @EnumItem(ordinal = 2) TAILS(2),

  /** 双面 */
  @EnumItem(ordinal = 3) DOUBLE(3),

  /** 所有 */
  @EnumItem(ordinal = 4) ALL(4),

  /** 所有内容 */
  @EnumItem(ordinal = 5) ALL_CONTENT(5),

  /**
   * 完整的
   *
   * 针对于视频，音频等等……
   */
  @EnumItem(ordinal = 6) INTACT(6);

  override val value: Int = v

  companion object {
    @JvmStatic fun findVal(value: Int?) = entries.find { value == it.v }

    @JvmStatic operator fun get(v: Int?) = findVal(v)
  }
}
