/*
 *  Copyright (c) 2020-2024 TrueNine. All rights reserved.
 *
 * The following source code is owned, developed and copyrighted by TrueNine
 * (truenine304520@gmail.com) and represents a substantial investment of time, effort,
 * and resources. This software and its components are not to be used, reproduced,
 * distributed, or sublicensed in any form without the express written consent of
 * the copyright owner, except as permitted by law.
 * Any unauthorized use, distribution, or modification of this source code,
 * or any portion thereof, may result in severe civil and criminal penalties,
 * and will be prosecuted to the maximum extent possible under the law.
 * For inquiries regarding usage or redistribution, please contact:
 *     TrueNine
 *     email: <truenine304520@gmail.com>
 *     website: <github.com/TrueNine>
 */
package net.yan100.compose.rds.core.typing

import com.fasterxml.jackson.annotation.JsonValue
import net.yan100.compose.core.typing.IntTyping
import net.yan100.compose.rds.core.typing.AttachmentTyping.entries
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
enum class AttachmentTyping(v: Int) : IntTyping {
  /** 文件 */
  @EnumItem(ordinal = 0)
  ATTACHMENT(0),

  /** 根路径 */
  @EnumItem(ordinal = 1)
  BASE_URL(1);

  @JsonValue
  override val value = v

  companion object {
    @JvmStatic
    @Deprecated("use operation get", replaceWith = ReplaceWith("get"))
    fun findVal(v: Int?) = entries.find { it.value == v }

    @JvmStatic
    operator fun get(v: Int?) = findVal(v)
  }
}
