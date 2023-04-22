package com.truenine.component.security.defaults

import com.truenine.component.core.lang.EmptyDefaultModel
import com.truenine.component.core.lang.LogKt
import com.truenine.component.core.models.UserAuthorizationInfoModel
import com.truenine.component.security.spring.security.SecurityPreflightValidFilter
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse

class EmptyPreflightValidFilter : EmptyDefaultModel, SecurityPreflightValidFilter() {

  private val log = LogKt.getLog(this::class)

  init {
    log.warn("正在使用默认的jwt过滤器")
  }

  override fun getUserAuthorizationInfo(
    token: String?,
    reFlashToken: String?,
    request: HttpServletRequest,
    response: HttpServletResponse
  ): UserAuthorizationInfoModel {
    log.warn("生成了一个空的 {}", ::UserAuthorizationInfoModel.name)
    return UserAuthorizationInfoModel()
  }
}
