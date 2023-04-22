package com.daojiatech.center.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.truenine.component.core.lang.requireAll
import com.truenine.component.core.models.ReFlushTokenModel
import com.truenine.component.core.models.UserAuthorizationInfoModel
import com.truenine.component.security.exceptions.SecurityException
import com.truenine.component.security.jwt.JwtVerifier
import com.truenine.component.security.jwt.consts.VerifierParamModel
import com.truenine.component.security.spring.security.SecurityPreflightValidFilter
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse

class PreValidFilter(
  private val mapper: ObjectMapper,
  private val jwtVerifier: JwtVerifier
) : SecurityPreflightValidFilter() {
  override fun getUserAuthorizationInfo(
    token: String?,
    reFlashToken: String?,
    request: HttpServletRequest,
    response: HttpServletResponse
  ): UserAuthorizationInfoModel {
    // 先校验 authToken 是否过期
    // 再校验 exp 是否过期
    val authToken = token?.let {
      jwtVerifier.verify(VerifierParamModel(token = it, encryptDataTargetType = UserAuthorizationInfoModel::class.java)).decryptedData
    }
    val exp = reFlashToken?.let {
      jwtVerifier.verify(VerifierParamModel(token = it, encryptDataTargetType = ReFlushTokenModel::class.java)).decryptedData
    }
    if (null == authToken || null == exp) {
      throw SecurityException("异常行为")
    }

    runCatching {
      requireAll(
        exp.userId == authToken.userId,
        exp.deviceId == authToken.deviceId,
        exp.loginIpAddr == authToken.loginIpAddr,
      ) {
        "jwt验签失败 token $authToken exp = $exp"
      }
    }.onFailure {
      throw SecurityException(msg = it.message)
    }
    return authToken
  }
}
