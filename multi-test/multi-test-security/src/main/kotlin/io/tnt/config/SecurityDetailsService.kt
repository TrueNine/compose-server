package io.tnt.config

import com.truenine.component.core.models.UserAuthorizationInfoModel
import com.truenine.component.security.spring.security.SecurityUserDetailsService


class SecurityDetailsService(

) : SecurityUserDetailsService() {
  override fun loadUserDetailsByAccount(account: String?): UserAuthorizationInfoModel? {
    TODO("Not yet implemented")
  }
}
