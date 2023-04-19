package com.truenine.component.rds.service.aggregator

import com.truenine.component.rds.entity.UserEntity
import com.truenine.component.rds.models.request.LoginAccountRequestParam
import com.truenine.component.rds.models.request.ModifyAccountPasswordRequestParam
import com.truenine.component.rds.models.request.RegisterAccountRequestParam
import java.time.LocalDateTime

interface AccountAggregator {

  /**
   * 注册账号
   */
  fun registerAccount(param: RegisterAccountRequestParam): Boolean

  /**
   * 登录指定账号，返回用户信息
   */
  fun login(param: LoginAccountRequestParam): UserEntity?

  /**
   * 根据账号修改密码
   */
  fun modifyPassword(param: ModifyAccountPasswordRequestParam): Boolean

  /**
   * 根据账号校验密码正确性
   */
  fun verifyPassword(account: String, password: String): Boolean

  /**
   * 封禁账号到指定时间
   */
  fun bannedAccountTo(account: String, dateTime: LocalDateTime)
}
