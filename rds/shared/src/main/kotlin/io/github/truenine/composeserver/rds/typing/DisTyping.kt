package io.github.truenine.composeserver.rds.typing

import io.github.truenine.composeserver.IIntTyping
import org.babyfish.jimmer.sql.EnumItem
import org.babyfish.jimmer.sql.EnumType

/**
 * # 第二、三代中国残疾证残疾类别
 *
 * @author TrueNine
 * @since 2023-11-03
 */
@EnumType(EnumType.Strategy.ORDINAL)
enum class DisTyping(typ: Int) : IIntTyping {
  /** 视力 */
  @EnumItem(ordinal = 1) EYE(1),

  /** 听力 */
  @EnumItem(ordinal = 2) EAR(2),

  /** 言语 */
  @EnumItem(ordinal = 3) MOUTH(3),

  /** 肢体 */
  @EnumItem(ordinal = 4) BODY(4),

  /** 智力 */
  @EnumItem(ordinal = 5) IQ(5),

  /** 精神 */
  @EnumItem(ordinal = 6) NERVE(6),

  /** 多重 */
  @EnumItem(ordinal = 7) MULTIPLE(7);

  override val value = typ

  companion object {
    @JvmStatic fun findVal(typ: Int?) = entries.find { it.value == typ }

    @JvmStatic operator fun get(v: Int?) = findVal(v)
  }
}
