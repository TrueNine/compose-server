package io.github.truenine.composeserver.rds.enums

import io.github.truenine.composeserver.IIntEnum
import org.babyfish.jimmer.sql.EnumItem
import org.babyfish.jimmer.sql.EnumType

/** Audit status */
@EnumType(EnumType.Strategy.ORDINAL)
enum class AuditTyping(v: Int) : IIntEnum {
  /** Not audited */
  @EnumItem(ordinal = 0) NONE(0),

  /** Assigned to an auditor, or already assigned and being processed */
  @EnumItem(ordinal = 1) ASSIGNED(1),

  /** Approved */
  @EnumItem(ordinal = 2) PASS(2),

  /** Not approved */
  @EnumItem(ordinal = 3) FAIL(3),

  /** Revoked */
  @EnumItem(ordinal = 4) CANCEL(4),

  /** Expired */
  @EnumItem(ordinal = 5) EXPIRED(5),

  /** Rejected */
  @EnumItem(ordinal = 6) REJECT(6),

  /** Hidden */
  @EnumItem(ordinal = 7) SHADOW_BAN(7);

  override val value: Int = v

  companion object {
    @JvmStatic fun findVal(value: Int?) = entries.find { it.value == value }

    @JvmStatic operator fun get(value: Int?) = findVal(value)
  }
}
