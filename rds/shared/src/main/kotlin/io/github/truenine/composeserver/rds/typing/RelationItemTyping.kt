package io.github.truenine.composeserver.rds.typing

import io.github.truenine.composeserver.typing.IntTyping
import org.babyfish.jimmer.sql.EnumItem
import org.babyfish.jimmer.sql.EnumType

/** 关系对象类型 */
@EnumType(EnumType.Strategy.ORDINAL)
enum class RelationItemTyping(v: Int) : IntTyping {
  /** 无 */
  @EnumItem(ordinal = 0) NONE(0),

  /** 用户 */
  @EnumItem(ordinal = 1) USER(1),

  /** 客户 */
  @EnumItem(ordinal = 2) CUSTOMER(2),

  /** 企业 */
  @EnumItem(ordinal = 3) ENTERPRISE(3),

  /** 员工 */
  @EnumItem(ordinal = 4) EMPLOYEE(4),

  /** 其他 */
  @EnumItem(ordinal = 9999) OTHER(9999);

  override val value: Int = v

  companion object {
    fun findVal(v: Int?) = entries.find { it.value == v }
  }
}
