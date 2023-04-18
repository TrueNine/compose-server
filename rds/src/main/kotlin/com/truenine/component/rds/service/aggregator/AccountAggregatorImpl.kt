package com.truenine.component.rds.service.aggregator

import com.truenine.component.rds.service.UserInfoService
import com.truenine.component.rds.service.UserService
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class AccountAggregatorImpl(
  private val userService: UserService,
  private val userInfoService: UserInfoService,
  private val passwordEncoder: PasswordEncoder
) : AccountAggregator {

}
