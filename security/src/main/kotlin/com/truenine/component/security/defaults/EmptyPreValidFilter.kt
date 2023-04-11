package com.truenine.component.security.defaults

import com.truenine.component.core.lang.EmptyDefaultModel
import com.truenine.component.core.lang.LogKt
import com.truenine.component.core.models.UserAuthorizationInfoModel
import com.truenine.component.security.spring.security.SecurityPreValidFilter
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse

class EmptyPreValidFilter : EmptyDefaultModel, SecurityPreValidFilter() {

  private val log = LogKt.getLog(this::class)

  init {
    log.warn("正在使用默认的jwt过滤器")
  }

  override fun getUserAuthorizationInfo(
    token: String?,
    reFlash: String?,
    request: HttpServletRequest,
    response: HttpServletResponse
  ): UserAuthorizationInfoModel {
    log.trace("正在使用空体")
    return UserAuthorizationInfoModel()
  }
}
