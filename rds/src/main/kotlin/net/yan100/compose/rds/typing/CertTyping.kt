/*
 * ## Copyright (c) 2024 TrueNine. All rights reserved.
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
 *     Email: <truenine304520@gmail.com>
 *     Website: [gitee.com/TrueNine]
 */
package net.yan100.compose.rds.typing

import com.fasterxml.jackson.annotation.JsonValue
import io.swagger.v3.oas.annotations.media.Schema
import net.yan100.compose.core.lang.IntTyping

@Schema(title = "证件类型")
enum class CertTyping(private val v: Int) : IntTyping {
  @Schema(title = "无具体类型") NONE(0),
  @Schema(title = "身份证") ID_CARD(1),
  @Schema(title = "二代身份证") IC_CARD2(2),
  @Schema(title = "残疾证") DISABILITY_CARD(3),
  @Schema(title = "二代残疾证") DISABILITY_CARD2(4),
  @Schema(title = "三代残疾卡") DISABILITY_CARD3(5),
  @Schema(title = "户口") HOUSEHOLD_CARD(6),
  @Schema(title = "银行卡") BANK_CARD(7),
  @Schema(title = "合同") CONTRACT(8),
  @Schema(title = "营业执照") BIZ_LICENSE(9),
  @Schema(title = "寸照") TITLE_IMAGE(10),
  @Schema(title = "个人所得税状况录屏") PERSONAL_INCOME_TAX_VIDEO(11);

  @JsonValue override val value: Int = v

  companion object {
    @JvmStatic fun findVal(v: Int?) = CertTyping.entries.find { it.value == v }
  }
}
