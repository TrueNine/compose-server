package net.yan100.compose.rds.typing

import io.swagger.v3.oas.annotations.media.Schema
import net.yan100.compose.core.lang.IntTyping

@Schema(title = "审核状态")
enum class AuditTyping(
  val v: Int
) : IntTyping {
  @Schema(title = "未审核")
  NONE(0),

  @Schema(title = "分配给审核员")
  ASSIGNED(1),

  @Schema(title = "审核通过")
  PASS(2),

  @Schema(title = "审核未通过")
  FAIL(3),

  @Schema(title = "已撤销")
  CANCEL(4),

  @Schema(title = "已过期")
  EXPIRED(5),

  @Schema(title = "驳回")
  REJECT(6);

  override fun getValue(): Int? = v

  companion object {
    @JvmStatic
    fun findVal(value: Int?) = entries.find { it.v == value }
  }
}
