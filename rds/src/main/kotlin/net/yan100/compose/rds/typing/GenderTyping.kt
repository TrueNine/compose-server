package net.yan100.compose.rds.typing

import com.fasterxml.jackson.annotation.JsonValue
import io.swagger.v3.oas.annotations.media.Schema
import net.yan100.compose.core.lang.IntTyping

/**
 * 性别类型
 *
 * @author TrueNine
 * @since 2023-04-23
 */
@Schema(title = "性别")
enum class GenderTyping(
  private val value: Int
) : IntTyping {
  /**
   * 男
   */
  @Schema(title = "男")
  MAN(1),

  /**
   * 女
   */
  @Schema(title = "女")
  WOMAN(0),

  /**
   * 未知
   */
  @Schema(title = "未知")
  UNKNOWN(2);

  @JsonValue
  override fun getValue(): Int = value

  companion object {
    @JvmStatic
    fun findVal(v: Int?) = entries.find { it.value == v }
  }
}
