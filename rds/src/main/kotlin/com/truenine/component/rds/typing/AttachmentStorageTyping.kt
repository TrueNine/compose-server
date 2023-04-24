package com.truenine.component.rds.typing

import com.fasterxml.jackson.annotation.JsonValue
import io.swagger.v3.oas.annotations.media.Schema

/**
 * 附件存储类别
 *
 * @author TrueNine
 * @since 2023-04-23
 */
@Schema(title = "附件存储类别")
enum class AttachmentStorageTyping(
  private val v: String
) {
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
  fun getValue() = v

  companion object {
    @JvmStatic
    fun findVal(value: String?) = AttachmentStorageTyping.values().find { it.v == value }
  }
}
