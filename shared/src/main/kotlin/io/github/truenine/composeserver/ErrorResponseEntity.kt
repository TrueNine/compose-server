package io.github.truenine.composeserver

import io.github.truenine.composeserver.enums.HttpStatus

/**
 * # 新版 错误响应实体
 * > 相比之前，采用 kotlin data class 设计
 *
 * @param errorBy 错误枚举
 * @param code 错误码
 * @param msg 错误消息
 * @param alt 错误提示 (遇到此错误可以如何做）
 * @param debugSerialTrace 错误跟踪信息
 * @author TrueNine
 * @since 2025-03-01
 */
data class ErrorResponseEntity
@JvmOverloads
constructor(
  val errorBy: HttpStatus = HttpStatus.UNKNOWN,
  val code: Int? = errorBy.code,
  val msg: String? = errorBy.message,
  val alt: String? = null,
  val debugSerialTrace: Any? = null,
)
