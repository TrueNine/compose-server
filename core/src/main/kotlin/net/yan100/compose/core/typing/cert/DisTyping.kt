package net.yan100.compose.core.typing.cert

import com.fasterxml.jackson.annotation.JsonValue
import io.swagger.v3.oas.annotations.media.Schema
import net.yan100.compose.core.lang.IntTyping

/**
 * # 第二代残疾证残疾类别
 * @author TrueNine
 * @since 2023-11-03
 */
enum class DisTyping(private val typ: Int) : IntTyping {
  @Schema(title = "视力残障")
  EYE(1),

  @Schema(title = "听力残障")
  EAR(2),

  @Schema(title = "言语残障")
  MOUTH(3),

  @Schema(title = "肢体残障")
  BODY(4),

  @Schema(title = "智力残障")
  IQ(5),

  @Schema(title = "精神残障")
  NERVE(6),

  @Schema(title = "多重残障")
  MULTIPLE(7);

  @JsonValue
  override fun getValue() = typ

  companion object {
    @JvmStatic
    fun findVal(typ: Int?) = entries.find { it.typ == typ }
  }
}
