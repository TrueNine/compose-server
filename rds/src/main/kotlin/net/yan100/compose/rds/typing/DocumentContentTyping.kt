package net.yan100.compose.rds.typing

import com.fasterxml.jackson.annotation.JsonValue
import io.swagger.v3.oas.annotations.media.Schema
import net.yan100.compose.core.lang.IntTyping

@Schema(title = "证件内容类型")
enum class DocumentContentTyping(
  private val value: Int
) : IntTyping {
  @Schema(title = "无要求")
  NONE(0),

  @Schema(title = "图片")
  IMAGE(1),

  @Schema(title = "扫描件图片")
  SCANNED_IMAGE(2),

  @Schema(title = "截图")
  SCREEN_SHOT(3),

  @Schema(title = "视频")
  VIDEO(4),

  @Schema(title = "录音")
  RECORDING(5),

  @Schema(title = "复印件图片")
  COPYFILE_IMAGE(6),

  @Schema(title = "翻拍图片")
  REMAKE_IMAGE(7);

  @JsonValue
  override fun getValue(): Int = value

  companion object {
    @JvmStatic
    fun findVal(v: Int?) = DocumentContentTyping.entries.find { it.value == v }
  }
}
