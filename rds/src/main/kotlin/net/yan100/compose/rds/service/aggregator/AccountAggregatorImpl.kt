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
import java.time.LocalDateTime
import net.yan100.compose.core.alias.RefId
import net.yan100.compose.rds.core.entities.withNew
import net.yan100.compose.rds.entities.Usr
import net.yan100.compose.rds.entities.info.UserInfo
import net.yan100.compose.rds.service.IRoleGroupService
import net.yan100.compose.rds.service.IUserInfoService
import net.yan100.compose.rds.service.IUserService
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AccountAggregatorImpl(
  private val userService: IUserService,
  private val userInfoService: IUserInfoService,
  private val passwordEncoder: PasswordEncoder,
  private val roleGroupService: IRoleGroupService,
) : IAccountAggregator {

  // TODO 硬编码
  @Transactional(rollbackFor = [Exception::class])
  override fun assignAccount(
    @Valid usr: Usr,
    createUserId: RefId,
    @Valid userInfo: UserInfo?,
    roleGroup: Set<String>?,
  ): Usr {
    val savedUsr =
      usr.withNew().run {
        check(!userService.existsByAccount(account)) { "分配的账号已经存在" }
        pwdEnc = passwordEncoder.encode(this.pwdEnc)
        this.createUserId = createUserId
        userService.save(this)
      }

    userInfo?.withNew()?.also {
      it.createUserId = createUserId
      it.pri = true
      it.userId = savedUsr.id
      userInfoService.save(it)
    }

    roleGroup?.also { rg ->
      roleGroupService.assignPlainToUser(savedUsr.id)
      if (rg.contains("ADMIN")) roleGroupService.assignAdminToUser(savedUsr.id)
    }
    return savedUsr
  }

  @Transactional(rollbackFor = [Exception::class])
  internal fun saveUsrForRegisterParam(param: IAccountAggregator.RegisterAccountDto): Usr {
    return userService.save(
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
  override fun registerAccount(@Valid param: IAccountAggregator.RegisterAccountDto): Usr? =
    if (!userService.existsByAccount(param.account!!)) {
      saveUsrForRegisterParam(param).also {
        userInfoService.savePlainUserInfoByUser(it)
        roleGroupService.assignPlainToUser(it.id)
      }
    } else null

  @Transactional(rollbackFor = [Exception::class])
  override fun registerAccountForWxpa(
    param: IAccountAggregator.RegisterAccountDto,
    openId: String,
  ): Usr? =
    if (userInfoService.existsByWechatOpenId(openId)) {
      saveUsrForRegisterParam(param).also {
        roleGroupService.assignPlainToUser(it.id)
        userInfoService.savePlainUserInfoByUser(it).let { u ->
          u.wechatOpenid = openId
          userInfoService.save(u)
        }
      }
    } else null

  override fun login(@Valid param: IAccountAggregator.LoginAccountDto): Usr? =
    if (verifyPassword(param.account!!, param.password!!)) {
      userService.findUserByAccount(param.account!!)
    } else null

  override fun modifyPassword(@Valid param: IAccountAggregator.ModifyAccountPasswordDto): Boolean {
    if (!verifyPassword(param.account!!, param.oldPassword!!)) return false
    if (param.oldPassword == param.newPassword) return false
    val user = userService.findUserByAccount(param.account!!) ?: return false
    user.pwdEnc = passwordEncoder.encode(param.newPassword)
    userService.save(user)
    return true
  }

  override fun verifyPassword(account: String, password: String): Boolean {
    return if (userService.existsByAccount(account)) {
      val encodedPwd = userService.findPwdEncByAccount(account)
      return passwordEncoder.matches(password, encodedPwd)
    } else false
  }

  override fun bannedAccountTo(account: String, dateTime: LocalDateTime) = userService.modifyUserBandTimeTo(account, dateTime)
}
