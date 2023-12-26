package net.yan100.compose.rds.typing

import com.fasterxml.jackson.annotation.JsonValue
import io.swagger.v3.oas.annotations.media.Schema
import net.yan100.compose.core.lang.IntTyping

@Schema(title = "关系对象类型")
enum class RelationItemTyping(
  private val v: Int
) : IntTyping {
  @Schema(title = "无")
  NONE(0),

  @Schema(title = "用户")
  USER(1),

  @Schema(title = "客户")
  CUSTOMER(2),

  @Schema(title = "企业")
  ENTERPRISE(3),

  @Schema(title = "员工")
  EMPLOYEE(4),

  @Schema(title = "其他")
  OTHER(9999);

  @JsonValue
  override fun getValue(): Int = v

  companion object {
    fun findVal(v: Int?) = entries.find { it.v == v }
  }
}
