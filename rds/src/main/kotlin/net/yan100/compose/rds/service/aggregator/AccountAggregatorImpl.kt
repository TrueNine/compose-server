package net.yan100.compose.rds.service.aggregator

import jakarta.validation.Valid
import net.yan100.compose.rds.entity.User
import net.yan100.compose.rds.models.req.LoginAccountReq
import net.yan100.compose.rds.models.req.ModifyAccountPasswordReq
import net.yan100.compose.rds.models.req.RegisterAccountReq
import net.yan100.compose.rds.service.IRoleGroupService
import net.yan100.compose.rds.service.IUserService
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class AccountAggregatorImpl(
  private val userService: IUserService,
  private val passwordEncoder: PasswordEncoder,
  private val roleGroupService: IRoleGroupService
) : AccountAggregator {

  override fun registerAccount(@Valid param: RegisterAccountReq): User? =
    if (!userService.existsByAccount(param.account!!)) {
      userService.save(User().apply {
        account = param.account
        pwdEnc = passwordEncoder.encode(param.password)
        nickName = param.nickName
        doc = param.description
      }).also { roleGroupService.assignPlainToUser(it.id!!) }
    } else null

  override fun login(@Valid param: LoginAccountReq): User? =
    if (verifyPassword(param.account!!, param.password!!)) {
      userService.findUserByAccount(param.account!!)
    } else null


  override fun modifyPassword(@Valid param: ModifyAccountPasswordReq): Boolean {
    if (!verifyPassword(param.account!!, param.oldPassword!!)) {
      return false
    }
    if (param.oldPassword == param.newPassword) {
      return false
    }
    val user = userService.findUserByAccount(param.account!!) ?: return false
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
