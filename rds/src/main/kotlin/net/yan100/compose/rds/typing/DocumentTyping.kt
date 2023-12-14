package net.yan100.compose.rds.typing

import com.fasterxml.jackson.annotation.JsonValue
import io.swagger.v3.oas.annotations.media.Schema
import net.yan100.compose.core.lang.IntTyping

@Schema(title = "证件类型")
enum class DocumentTyping(
  private val value: Int
) : IntTyping {
  @Schema(title = "无具体类型")
  NONE(0),

  @Schema(title = "身份证")
  ID_CARD(1),

  @Schema(title = "二代身份证")
  IC_CARD2(2),

  @Schema(title = "残疾证")
  DISABILITY_CARD(3),

  @Schema(title = "二代残疾证")
  DISABILITY_CARD2(4),

  @Schema(title = "三代残疾卡")
  DISABILITY_CARD3(5),

  @Schema(title = "户口")
  HOUSEHOLD_CARD(6),

  @Schema(title = "银行卡")
  BANK_CARD(7),

  @Schema(title = "合同")
  CONTRACT(8),

  @Schema(title = "营业执照")
  BIZ_LICENSE(9),

  @Schema(title = "寸照")
  TITLE_IMAGE(10);

  @JsonValue
  override fun getValue(): Int = value

  companion object {
    @JvmStatic
    fun findVal(v: Int?) = DocumentTyping.entries.find { it.value == v }
  }
}
