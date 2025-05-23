package net.yan100.compose.rds.typing

import net.yan100.compose.typing.IntTyping
import org.babyfish.jimmer.sql.EnumItem
import org.babyfish.jimmer.sql.EnumType

/** ## 用户信息所属的常规血型 */
@EnumType(EnumType.Strategy.ORDINAL)
enum class BloodTyping(v: Int) : IntTyping {
  /** A型 */
  @EnumItem(ordinal = 1)
  A(1),

  /** B型 */
  @EnumItem(ordinal = 2)
  B(2),

  /** AB型 */
  @EnumItem(ordinal = 3)
  AB(3),

  /** O型 */
  @EnumItem(ordinal = 4)
  O(4),

  /** 其他血型 */
  @EnumItem(ordinal = 9999)
  OTHER(9999);

  override val value: Int = v

  companion object {
    @JvmStatic
    fun findVal(v: Int?) = BloodTyping.entries.find { it.value == v }

    @JvmStatic
    fun get(v: Int?) = findVal(v)
  }
}
