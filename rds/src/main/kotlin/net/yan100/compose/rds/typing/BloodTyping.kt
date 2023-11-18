package net.yan100.compose.rds.typing

import com.fasterxml.jackson.annotation.JsonValue
import io.swagger.v3.oas.annotations.media.Schema
import net.yan100.compose.core.lang.IntTyping

@Schema(title = "血型")
enum class BloodTyping(
  private val value: Int
) : IntTyping {
  @Schema(title = "A型")
  A(1),

  @Schema(title = "B型")
  B(2),

  @Schema(title = "AB型")
  AB(3),

  @Schema(title = "O型")
  O(4),

  @Schema(title = "其他血型")
  OTHER(9999);

  @JsonValue
  override fun getValue(): Int? {
    return this.value
  }

  companion object {
    @JvmStatic
    fun findVal(v: Int?) = BloodTyping.entries.find { it.value == v }
  }
}
