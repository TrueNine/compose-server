package net.yan100.compose.rds.crud.service.aggregator

import java.time.LocalDateTime
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid
import net.yan100.compose.core.RefId
import net.yan100.compose.core.generator.IOrderCodeGenerator
import net.yan100.compose.core.hasText
import net.yan100.compose.rds.core.annotations.ACID
import net.yan100.compose.rds.core.entities.withNew
import net.yan100.compose.rds.crud.entities.jpa.UserAccount
import net.yan100.compose.rds.crud.entities.jpa.UserInfo
import net.yan100.compose.rds.crud.service.IUserAccountService
import net.yan100.compose.rds.crud.service.IUserInfoService
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AccountAggregatorImpl(
  private val userService: IUserAccountService,
  private val bizCodeGen: IOrderCodeGenerator,
  private val userInfoService: IUserInfoService,
  private val passwordEncoder: PasswordEncoder,
  private val roleGroupService:
    net.yan100.compose.rds.crud.service.IRoleGroupService,
) : IAccountAggregator {

  @OptIn(ExperimentalUuidApi::class)
  @ACID
  @Deprecated("触发了脏跟踪特性")
  override fun assignAccountToUserInfo(
    createUserId: RefId,
    userInfoId: RefId,
  ): UserAccount? {
    return if (
      userInfoService.foundById(userInfoId) &&
        !userService.foundByUserInfoId(userInfoId)
    ) {
      userInfoService.fetchById(userInfoId)?.let { info ->
        check(info.firstName.hasText()) { "姓名为空，不能转换为呢称" }
        check(info.lastName.hasText()) { "姓名为空，不能转换为呢称" }
        info.pri = true

        val account =
          UserAccount().run {
            this.createUserId = createUserId
            nickName = info.firstName + info.lastName
            account = bizCodeGen.nextString()
            pwdEnc = passwordEncoder.encode(Uuid.random().toHexString())
            userService.postFound(this)
          }
        val saveAccount = userService.postFound(account)
        info.userId = saveAccount.id
        saveAccount
      }
    } else null
  }

  // TODO 硬编码
  @ACID
  override fun assignAccount(
    usr: UserAccount,
    createUserId: RefId,
    userInfo: UserInfo?,
    roleGroup: Set<String>?,
  ): UserAccount {
    val savedUsr =
      usr.withNew().run {
        check(!userService.existsByAccount(account)) { "分配的账号已经存在" }
        pwdEnc = passwordEncoder.encode(this.pwdEnc)
        this.createUserId = createUserId
        userService.post(this)
      }

    userInfo?.withNew()?.also {
      it.createUserId = createUserId
      it.pri = true
      it.userId = savedUsr.id
      userInfoService.post(it)
    }

    roleGroup?.also { rg ->
      roleGroupService.assignPlainToUser(savedUsr.id)
      if (rg.contains("ADMIN")) roleGroupService.assignAdminToUser(savedUsr.id)
    }
    return savedUsr
  }

  @ACID
  internal fun saveUsrForRegisterParam(
    param: IAccountAggregator.RegisterDto
  ): UserAccount {
    return userService.post(
      UserAccount().withNew().apply {
        checkNotNull(param.createUserId) { "创建此用户的用户 id 不能为空" }
        createUserId = param.createUserId!!
        account = param.account!!
        pwdEnc = passwordEncoder.encode(param.password)
        nickName = param.nickName
        doc = param.description
      }
    )
  }

  @ACID
  override fun registerAccount(
    param: IAccountAggregator.RegisterDto
  ): UserAccount? =
    if (!userService.existsByAccount(param.account!!)) {
      saveUsrForRegisterParam(param).also {
        userInfoService.savePlainUserInfoByUser(it)
        roleGroupService.assignPlainToUser(it.id)
      }
    } else null

  @Transactional(rollbackFor = [Exception::class])
  override fun registerAccountForWxpa(
    param: IAccountAggregator.RegisterDto,
    openId: String,
  ): UserAccount? =
    if (!userInfoService.existsByWechatOpenId(openId)) {
      saveUsrForRegisterParam(param).also {
        roleGroupService.assignPlainToUser(it.id)
        userInfoService.savePlainUserInfoByUser(it).let { u ->
          u.wechatOpenid = openId
          userInfoService.post(u)
        }
      }
    } else null

  override fun login(param: IAccountAggregator.LoginDto): UserAccount? =
    if (verifyPassword(param.account!!, param.password!!)) {
      userService.fetchByAccount(param.account!!)
    } else null

  override fun modifyPassword(
    param: IAccountAggregator.ModifyPasswordDto
  ): Boolean {
    if (!verifyPassword(param.account!!, param.oldPassword!!)) return false
    if (param.oldPassword == param.newPassword) return false
    val user = userService.fetchByAccount(param.account!!) ?: return false
    user.pwdEnc = passwordEncoder.encode(param.newPassword)
    userService.post(user)
    return true
  }

  override fun verifyPassword(account: String, password: String): Boolean {
    return if (userService.existsByAccount(account)) {
      val encodedPwd = userService.findPwdEncByAccount(account)
      return passwordEncoder.matches(password, encodedPwd)
    } else false
  }

  override fun banWith(account: String, dateTime: LocalDateTime) =
    userService.modifyUserBandTimeTo(account, dateTime)
}
