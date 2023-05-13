package net.yan100.compose.rds.typing

import com.fasterxml.jackson.annotation.JsonValue
import io.swagger.v3.oas.annotations.media.Schema
import net.yan100.compose.core.lang.StringTyping

/**
 * 附件存储类别
 *
 * @author TrueNine
 * @since 2023-04-23
 */
@Schema(title = "附件存储类别")
enum class AttachmentStorageTyping(
  private val value: String
) : StringTyping {
  /**
   * 远程存储
   */
  @Schema(title = "远程存储 Remote")
  REMOTE("R"),

  /**
   * 本地存储
   */
  @Schema(title = "本地存储 Native")
  NATIVE("N");

  @JsonValue
  override fun getValue() = value

  companion object {
    @JvmStatic
    fun findVal(v: String?) = AttachmentStorageTyping.values().find { it.value == v }
  }
}
