package io.github.truenine.composeserver.rds.enums

import io.github.truenine.composeserver.IIntEnum
import org.babyfish.jimmer.sql.EnumItem
import org.babyfish.jimmer.sql.EnumType
import org.babyfish.jimmer.sql.EnumType.Strategy

/**
 * Attachment categories
 *
 * @author TrueNine
 * @since 2023-04-23
 */
@EnumType(Strategy.ORDINAL)
enum class AttachmentTyping(v: Int) : IIntEnum {
  /** File */
  @EnumItem(ordinal = 0) ATTACHMENT(0),

  /** Root path */
  @EnumItem(ordinal = 1) BASE_URL(1);

  override val value = v

  companion object {
    @JvmStatic @Deprecated("use operation get", replaceWith = ReplaceWith("get")) fun findVal(v: Int?) = entries.find { it.value == v }

    @JvmStatic operator fun get(v: Int?) = findVal(v)
  }
}
