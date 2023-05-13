package net.yan100.compose.rds.service.aggregator

import jakarta.validation.Valid
import net.yan100.compose.rds.models.request.LoginAccountRequestParam
import net.yan100.compose.rds.models.request.ModifyAccountPasswordRequestParam
import net.yan100.compose.rds.models.request.RegisterAccountRequestParam
import net.yan100.compose.rds.service.UserService
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class AccountAggregatorImpl(
  private val userService: UserService,
  private val passwordEncoder: PasswordEncoder
) : AccountAggregator {

  override fun registerAccount(@Valid param: RegisterAccountRequestParam): Boolean =
    if (!userService.existsByAccount(param.account)) {
      userService.save(net.yan100.compose.rds.entity.UserEntity().apply {
        account = param.account
        pwdEnc = passwordEncoder.encode(param.password)
        nickName = param.nickName
        doc = param.description
      }); true
    } else false

  override fun login(@Valid param: LoginAccountRequestParam): net.yan100.compose.rds.entity.UserEntity? =
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
