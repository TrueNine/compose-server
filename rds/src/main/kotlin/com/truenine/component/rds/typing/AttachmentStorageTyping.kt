package com.truenine.component.rds.typing

import com.fasterxml.jackson.annotation.JsonValue
import io.swagger.v3.oas.annotations.media.Schema

@Schema(title = "文件存储类别")
enum class AttachmentStorageTyping(
  private val v: String
) {
  /**
   * 本地存储
   */
  LOCAL("R"),

  /**
   * 远程存储
   */
  REMOTE("N");

  @JsonValue
  fun getValue() = v

  companion object {
    @JvmStatic
    fun findVal(value: String?) = AttachmentStorageTyping.values().find { it.v == value }
  }
}
