package net.yan100.compose.security.defaults

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import net.yan100.compose.core.lang.slf4j
import net.yan100.compose.core.models.UserAuthorizationInfoModel
import net.yan100.compose.security.spring.security.SecurityPreflightValidFilter

class EmptyPreflightValidFilter : net.yan100.compose.core.lang.EmptyDefaultModel, SecurityPreflightValidFilter() {

  private val log = slf4j(this::class)

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