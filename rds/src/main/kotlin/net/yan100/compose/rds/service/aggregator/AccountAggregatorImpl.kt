/*
 *  Copyright (c) 2020-2024 TrueNine. All rights reserved.
 *
 * The following source code is owned, developed and copyrighted by TrueNine
 * (truenine304520@gmail.com) and represents a substantial investment of time, effort,
 * and resources. This software and its components are not to be used, reproduced,
 * distributed, or sublicensed in any form without the express written consent of
 * the copyright owner, except as permitted by law.
 * Any unauthorized use, distribution, or modification of this source code,
 * or any portion thereof, may result in severe civil and criminal penalties,
 * and will be prosecuted to the maximum extent possible under the law.
 * For inquiries regarding usage or redistribution, please contact:
 *     TrueNine
 *     email: <truenine304520@gmail.com>
 *     website: <github.com/TrueNine>
 */
package net.yan100.compose.rds.service.aggregator

import jakarta.validation.Valid
import net.yan100.compose.core.RefId
import net.yan100.compose.core.generator.IOrderCodeGenerator
import net.yan100.compose.core.hasText
import net.yan100.compose.rds.core.entities.withNew
import net.yan100.compose.rds.entities.UserInfo
import net.yan100.compose.rds.entities.Usr
import net.yan100.compose.rds.service.IRoleGroupService
import net.yan100.compose.rds.service.IUserInfoService
import net.yan100.compose.rds.service.IUserService
import net.yan100.compose.security.crypto.Keys
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class AccountAggregatorImpl(
  private val userService: IUserService,
  private val bizCodeGen: IOrderCodeGenerator,
  private val userInfoService: IUserInfoService,
  private val passwordEncoder: PasswordEncoder,
  private val roleGroupService: IRoleGroupService,
) : IAccountAggregator {

  @Deprecated("触发了脏跟踪特性")
  @Transactional(rollbackFor = [Exception::class])
  override fun assignAccountToUserInfo(createUserId: RefId, userInfoId: RefId): Usr? {
    return if (userInfoService.foundById(userInfoId) && !userService.existsByUserInfoId(userInfoId)) {
      userInfoService.fetchById(userInfoId)?.let { info ->
        check(info.firstName.hasText()) { "姓名为空，不能转换为呢称" }
        check(info.lastName.hasText()) { "姓名为空，不能转换为呢称" }
        info.pri = true

        val account =
          Usr().run {
            this.createUserId = createUserId
            nickName = info.firstName + info.lastName
            account = bizCodeGen.nextString()
            pwdEnc = passwordEncoder.encode(Keys.generateRandomAsciiString())
            userService.postFound(this)
          }
        val saveAccount = userService.postFound(account)
        info.userId = saveAccount.id
        saveAccount
      }
    } else null
  }

  // TODO 硬编码
  @Transactional(rollbackFor = [Exception::class])
  override fun assignAccount(@Valid usr: Usr, createUserId: RefId, @Valid userInfo: UserInfo?, roleGroup: Set<String>?): Usr {
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

  @Transactional(rollbackFor = [Exception::class])
  internal fun saveUsrForRegisterParam(param: IAccountAggregator.RegisterDto): Usr {
    return userService.post(
      Usr().withNew().apply {
        checkNotNull(param.createUserId) { "创建此用户的用户 id 不能为空" }
        createUserId = param.createUserId!!
        account = param.account!!
        pwdEnc = passwordEncoder.encode(param.password)
        nickName = param.nickName
        doc = param.description
      }
    )
  }

  @Transactional(rollbackFor = [Exception::class])
  override fun registerAccount(@Valid param: IAccountAggregator.RegisterDto): Usr? =
    if (!userService.existsByAccount(param.account!!)) {
      saveUsrForRegisterParam(param).also {
        userInfoService.savePlainUserInfoByUser(it)
        roleGroupService.assignPlainToUser(it.id)
      }
    } else null

  @Transactional(rollbackFor = [Exception::class])
  override fun registerAccountForWxpa(param: IAccountAggregator.RegisterDto, openId: String): Usr? =
    if (!userInfoService.existsByWechatOpenId(openId)) {
      saveUsrForRegisterParam(param).also {
        roleGroupService.assignPlainToUser(it.id)
        userInfoService.savePlainUserInfoByUser(it).let { u ->
          u.wechatOpenid = openId
          userInfoService.post(u)
        }
      }
    } else null

  override fun login(@Valid param: IAccountAggregator.LoginDto): Usr? =
    if (verifyPassword(param.account!!, param.password!!)) {
      userService.findUserByAccount(param.account!!)
    } else null

  override fun modifyPassword(@Valid param: IAccountAggregator.ModifyPasswordDto): Boolean {
    if (!verifyPassword(param.account!!, param.oldPassword!!)) return false
    if (param.oldPassword == param.newPassword) return false
    val user = userService.findUserByAccount(param.account!!) ?: return false
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

  override fun banWith(account: String, dateTime: LocalDateTime) = userService.modifyUserBandTimeTo(account, dateTime)
}
