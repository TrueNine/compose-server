package com.truenine.component.core.autoconfig

import com.truenine.component.core.ctx.UserInfoContextHolder
import com.truenine.component.core.http.Headers
import com.truenine.component.core.http.InterAddressUtil
import com.truenine.component.core.lang.LogKt
import com.truenine.component.core.models.BasicUserInfoModel
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.context.annotation.Configuration
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.web.servlet.HandlerInterceptor
import org.springframework.web.servlet.ModelAndView
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer


@Configuration
@Order(Ordered.LOWEST_PRECEDENCE)
class BasicUserInfoInterceptor : WebMvcConfigurer, HandlerInterceptor {
  override fun addInterceptors(registry: InterceptorRegistry) {
    registry.addInterceptor(this)
  }

  override fun preHandle(
    request: HttpServletRequest,
    response: HttpServletResponse,
    handler: Any
  ): Boolean {
    return true
  }

  override fun postHandle(
    request: HttpServletRequest,
    response: HttpServletResponse,
    handler: Any,
    modelAndView: ModelAndView?
  ) {
    val userInfo = UserInfoContextHolder.get()
    log.debug("请求 URL = {}", request.requestURI)
    log.debug("当前拥有用户信息 = {}", userInfo)
    if (null == userInfo) {
      log.debug("当前用户信息为空，设置一个默认的用户信息")
      val newInfo = BasicUserInfoModel().apply {
        currentIpAddr = InterAddressUtil.getRequestIpAddress(request)
        userId = null
        loginIpAddr = null
        deviceId = request.getHeader(Headers.X_DEVICE_ID)
      }
      UserInfoContextHolder.set(newInfo)
    }
  }

  override fun afterCompletion(
    request: HttpServletRequest,
    response: HttpServletResponse,
    handler: Any,
    ex: Exception?
  ) {
    log.debug("清除当前用户信息 = {}", UserInfoContextHolder.get())
    UserInfoContextHolder.clean()
  }

  private val log = LogKt.getLog(BasicUserInfoInterceptor::class)
}
