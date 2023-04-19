package com.truenine.component.rds.service.aggregator

import com.truenine.component.rds.entity.UserEntity
import com.truenine.component.rds.models.request.LoginAccountRequestParam
import com.truenine.component.rds.models.request.ModifyAccountPasswordRequestParam
import com.truenine.component.rds.models.request.RegisterAccountRequestParam
import com.truenine.component.rds.service.UserInfoService
import com.truenine.component.rds.service.UserService
import jakarta.validation.Valid
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class AccountAggregatorImpl(
  private val userService: UserService,
  private val userInfoService: UserInfoService,
  private val passwordEncoder: PasswordEncoder
) : AccountAggregator {

  override fun registerAccount(@Valid param: RegisterAccountRequestParam): Boolean =
    if (userService.existsByAccount(param.account)) {
      false
    } else {
      val un = userService.save(UserEntity().apply {
        account = param.account
        pwdEnc = passwordEncoder.encode(param.password)
        nickName = param.nickName
        doc = param.description
      })
      un != null
    }

  override fun login(@Valid param: LoginAccountRequestParam): UserEntity? =
    if (verifyPassword(param.account, param.password)) {
      userService.findUserByAccount(param.account)
    } else null


  override fun modifyPassword(@Valid param: ModifyAccountPasswordRequestParam): Boolean {
    if (!verifyPassword(param.account, param.oldPassword)) {
      return false
    }
    if (param.oldPassword == param.newPassword) {
      return false
    }
    val user = userService.findUserByAccount(param.account) ?: return false
    user.pwdEnc = passwordEncoder.encode(param.newPassword)
    userService.save(user)
    return true
  }

  override fun verifyPassword(account: String, password: String): Boolean {
    return if (userService.existsByAccount(account)) {
      val encodedPwd = userService.findPwdEncByAccount(account)
      return passwordEncoder.matches(
        password,
        encodedPwd
      )
    } else false
  }

  override fun bannedAccountTo(account: String, dateTime: LocalDateTime) = userService.modifyUserBandTimeTo(account, dateTime)
}
