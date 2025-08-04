package io.github.truenine.composeserver.rds.enums

import io.github.truenine.composeserver.IIntEnum
import org.babyfish.jimmer.sql.EnumItem
import org.babyfish.jimmer.sql.EnumType
import org.babyfish.jimmer.sql.EnumType.Strategy

/**
 * 附件类别
 *
 * @author TrueNine
 * @since 2023-04-23
 */
@EnumType(Strategy.ORDINAL)
enum class AttachmentTyping(v: Int) : IIntEnum {
  /** 文件 */
  @EnumItem(ordinal = 0) ATTACHMENT(0),

  /** 根路径 */
  @EnumItem(ordinal = 1) BASE_URL(1);

  override val value = v

  companion object {
    @JvmStatic @Deprecated("use operation get", replaceWith = ReplaceWith("get")) fun findVal(v: Int?) = entries.find { it.value == v }

    @JvmStatic operator fun get(v: Int?) = findVal(v)
  }
}
