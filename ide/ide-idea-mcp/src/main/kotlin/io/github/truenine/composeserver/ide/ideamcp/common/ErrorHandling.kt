package io.github.truenine.composeserver.ide.ideamcp.common

import kotlinx.serialization.Serializable

/**
 * 错误详情
 */
@Serializable
data class ErrorDetails(
  /** 错误类型 */
  val type: String,
  /** 错误消息 */
  val message: String,
  /** 建议解决方案 */
  val suggestions: List<String> = emptyList()
)
