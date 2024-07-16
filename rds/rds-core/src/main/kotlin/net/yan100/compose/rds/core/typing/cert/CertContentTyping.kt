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
package net.yan100.compose.rds.core.typing.cert

import com.fasterxml.jackson.annotation.JsonValue
import io.swagger.v3.oas.annotations.media.Schema
import net.yan100.compose.core.typing.IntTyping

@Schema(title = "证件内容类型")
enum class CertContentTyping(private val v: Int) : IntTyping {
  @Schema(title = "无要求") NONE(0),
  @Schema(title = "图片") IMAGE(1),
  @Schema(title = "扫描件图片") SCANNED_IMAGE(2),
  @Schema(title = "截图") SCREEN_SHOT(3),
  @Schema(title = "视频") VIDEO(4),
  @Schema(title = "录音") RECORDING(5),
  @Schema(title = "复印件图片") COPYFILE_IMAGE(6),
  @Schema(title = "翻拍图片") REMAKE_IMAGE(7),
  @Schema(title = "处理过的扫描件") PROCESSED_SCANNED_IMAGE(8),
  @Schema(title = "处理过的图片") PROCESSED_IMAGE(9),
  @Schema(title = "处理过的视频") PROCESSED_VIDEO(10),
  @Schema(title = "处理过的音频") PROCESSED_AUDIO(11);

  @JsonValue override val value: Int = v

  companion object {
    @JvmStatic fun findVal(v: Int?) = CertContentTyping.entries.find { it.value == v }
  }
}
