package net.yan100.compose.rds.typing

import net.yan100.compose.typing.IntTyping
import org.babyfish.jimmer.sql.EnumItem
import org.babyfish.jimmer.sql.EnumType
import org.babyfish.jimmer.sql.EnumType.Strategy

/**
 * 性别类型
 *
 * @author TrueNine
 * @since 2023-04-23
 */
@EnumType(Strategy.ORDINAL)
enum class GenderTyping(private val v: Int) : IntTyping {
  /** 男 */
  @EnumItem(ordinal = 1)
  MAN(1),

  /** 女 */
  @EnumItem(ordinal = 0)
  WOMAN(0),

  /** 未知 */
  @EnumItem(ordinal = 9999)
  UNKNOWN(9999);

  override val value: Int = v

  companion object {
    @JvmStatic
    fun findVal(v: Int?) = entries.find { it.value == v }

    @JvmStatic
    operator fun get(v: Int?) = findVal(v)
  }
}
