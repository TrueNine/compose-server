package net.yan100.compose.rds.typing

import com.fasterxml.jackson.annotation.JsonValue
import io.swagger.v3.oas.annotations.media.Schema
import net.yan100.compose.core.lang.IntTyping

/**
 * 附件类别
 *
 * @author TrueNine
 * @since 2023-04-23
 */
@Schema(title = "附件类别")
enum class AttachmentTyping(
  private val value: Int
) : IntTyping {
  /**
   * 文件
   */
  @Schema(title = "文件")
  ATTACHMENT(0),

  /**
   * 根路径
   */
  @Schema(title = "根路径")
  BASE_URL(1);

  @JsonValue
  override fun getValue() = value

  companion object {
    @JvmStatic
    fun findVal(v: Int?) = entries.find { it.value == v }
  }
}
