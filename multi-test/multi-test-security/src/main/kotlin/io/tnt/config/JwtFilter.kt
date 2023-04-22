package com.daojiatech.center.security

import com.truenine.component.core.models.UserAuthorizationInfoModel
import com.truenine.component.security.spring.security.SecurityPreflightValidFilter
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse

class JwtFilter : SecurityPreflightValidFilter() {
  override fun getUserAuthorizationInfo(
    token: String?,
    reFlashToken: String?,
    request: HttpServletRequest,
    response: HttpServletResponse
  ): UserAuthorizationInfoModel {
    TODO("Not yet implemented")
  }
}
