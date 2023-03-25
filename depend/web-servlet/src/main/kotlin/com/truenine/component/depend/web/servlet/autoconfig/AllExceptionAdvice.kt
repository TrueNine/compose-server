package com.truenine.component.depend.web.servlet.autoconfig

import com.truenine.component.core.api.http.R
import com.truenine.component.core.api.http.Status
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
class AllExceptionAdvice {
  init {
    log.debug("注册 全局异常处理器 = {}", AllExceptionAdvice::class)
  }

  companion object {
    private val log = KtLogBridge.getLog(AllExceptionAdvice::class.java)
  }

  @ExceptionHandler(BasicBizException::class)
  fun basicBizException(bizEx: BasicBizException): R<*> {
    log.warn("业务模块异常", bizEx)
    return R.failed(bizEx, Status._400)
  }

  @ExceptionHandler(org.springframework.security.authentication.BadCredentialsException::class)
  fun badCredentialsException(ex: org.springframework.security.authentication.BadCredentialsException): R<*> {
    log.warn(
      "出现参数校验异常",
      ex
    )
    return R.failed(ex, 401)
  }

  /**
   * 全局兜底异常
   *
   * @param throwable 一切异常
   */
  @ExceptionHandler(Throwable::class)
  fun a(throwable: Throwable): R<*> {
    log.error(
      "服务器发生未知或者未经捕获异常, 异常类型 = {}",
      throwable.javaClass,
      throwable
    )
    return R.failed(throwable.message, Status._509)
  }
}
