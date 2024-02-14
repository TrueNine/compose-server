package net.yan100.compose.core.autoconfig

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import net.yan100.compose.core.ctx.UserInfoContextHolder
import net.yan100.compose.core.http.InterAddressUtil
import net.yan100.compose.core.lang.slf4j
import net.yan100.compose.core.models.RequestInfo
import org.springframework.context.annotation.Configuration
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.web.servlet.HandlerInterceptor
import org.springframework.web.servlet.ModelAndView
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

private val log = slf4j(BasicUserInfoInterceptor::class)

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

        log.trace("request URL = {}", request.requestURI)
        log.trace("userInfo = {}", userInfo)
        if (null == userInfo) {
            log.trace("当前用户信息为空，设置一个默认的用户信息")
            val newInfo = RequestInfo().apply {
                currentIpAddr = InterAddressUtil.getRequestIpAddress(request)
                userId = null
                loginIpAddr = null
                deviceId = request.getHeader(net.yan100.compose.core.http.Headers.X_DEVICE_ID)
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
        log.trace("清除当前用户信息 = {}", UserInfoContextHolder.get())
        UserInfoContextHolder.clean()
    }
}
