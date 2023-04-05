package com.truenine.component.depend.web.servlet.autoconfig

import com.truenine.component.core.http.ErrorMessage
import com.truenine.component.core.http.ErrMsg
import com.truenine.component.core.exceptions.BasicBizException
import com.truenine.component.core.lang.KtLogBridge
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseBody

/**
 * spring 统一异常处理器
 *
 * @author TrueNine
 * @since 2023-02-20
 */
@Component
@ControllerAdvice
@ResponseBody
class BasicExceptionAdvice {
  init {
    log.debug("注册 异常处理器 = {}", BasicExceptionAdvice::class)
  }

  companion object {
    private val log = KtLogBridge.getLog(BasicExceptionAdvice::class.java)
  }

  @ExceptionHandler(BasicBizException::class)
  fun basicBizException(bizEx: BasicBizException): ErrorMessage {
    log.warn("业务异常", bizEx)
    return ErrorMessage.failedByClientError()
  }

  @ExceptionHandler(org.springframework.security.authentication.BadCredentialsException::class)
  fun badCredentialsException(ex: org.springframework.security.authentication.BadCredentialsException): ErrorMessage {
    log.warn(
      "出现参数凭据校验异常",
      ex
    )
    return ErrorMessage.failedByMessages(ErrMsg._401)
  }

  /**
   * 全局兜底异常
   *
   * @param throwable 一切异常
   */
  @ExceptionHandler(Throwable::class)
  fun a(throwable: Throwable): ErrorMessage {
    log.error(
      "服务器发生未知或者未经捕获异常, 异常类型 = {}",
      throwable.javaClass,
      throwable
    )
    return ErrorMessage.failedByUnknownError()
  }
}
