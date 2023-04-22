package com.daojiatech.center.config

import com.truenine.component.core.http.ErrMsg
import com.truenine.component.core.http.ErrorMessage
import com.truenine.component.core.lang.LogKt
import jakarta.servlet.http.HttpServletResponse
import org.springframework.stereotype.Component
import org.springframework.web.HttpMediaTypeNotSupportedException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseBody
import java.sql.SQLException

@Component
@ControllerAdvice
class ExceptionAdvice {
  private val log = LogKt.getLog(this::class)

  init {
    log.debug("注册全局异常拦截器")
  }

  /**
   * 兜底异常
   */
  @ResponseBody
  @ExceptionHandler(java.lang.Exception::class)
  fun bottomOfThePocket(
    ex: java.lang.Exception,
    resp: HttpServletResponse
  ): ErrorMessage {
    resp.status = ErrMsg.UNKNOWN_ERROR.code
    log.error("服务器发生未知错误，请及时处理，如已知悉错误请及时捕获", ex)
    return ErrorMessage.failedBy(
      """
      ${ex.javaClass}
        
      ${ex.message}
    """.trimIndent(), ErrMsg.UNKNOWN_ERROR.code
    )
  }

  /**
   * 不支持的媒体类型
   */
  @ResponseBody
  @ExceptionHandler(HttpMediaTypeNotSupportedException::class)
  fun httpMediaTypeNotSupportedException(
    ex: HttpMediaTypeNotSupportedException,
    resp: HttpServletResponse
  ): ErrorMessage {
    log.warn("用户使用了不支持的媒体类型： {}", ex.contentType, ex)
    resp.status = ErrMsg._405.code
    return ErrorMessage.failedByMessages(ErrMsg._405)
  }

  /**
   * 参数校验异常
   */
  @ResponseBody
  @ExceptionHandler(MethodArgumentNotValidException::class)
  fun methodArgumentNotValidException(
    ex: MethodArgumentNotValidException,
    resp: HttpServletResponse
  ): ErrorMessage {
    resp.status = ErrMsg._400.code
    log.warn("服务器参数校验异常", ex)

    val errorMessage = ex.bindingResult.fieldErrors.map {
      it.defaultMessage
        ?: "${it.field} 参数校验异常"
    }.map { "$it \r\n" }
      .reduce { x, y ->
        x + y
      }
    return ErrorMessage.failedBy(errorMessage, ErrMsg._400.code)
  }

  /**
   * 数据库异常
   */
  @ResponseBody
  @ExceptionHandler(SQLException::class)
  fun sqlException(
    ex: SQLException,
    resp: HttpServletResponse
  ): ErrorMessage {
    resp.status = ErrMsg._500.code
    val msg = """
      SQL 错误：
      代码 ${ex.errorCode}
      状态 ${ex.sqlState}
      消息 ${ex.message}
    """.trimIndent()
    return ErrorMessage.failedBy(msg, 500)
  }

  @ResponseBody
  @ExceptionHandler(IllegalArgumentException::class)
  fun a(ex: IllegalArgumentException, resp: HttpServletResponse): ErrorMessage {
    resp.status = ErrMsg._400.code
    return ErrorMessage.failedBy(
      """
      参数错误：${ex.message}
    """.trimIndent(), ErrMsg._400.code
    )
  }
}
