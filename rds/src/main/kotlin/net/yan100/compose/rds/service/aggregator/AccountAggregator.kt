package net.yan100.compose.rds.service.aggregator

import net.yan100.compose.rds.entity.User
import net.yan100.compose.rds.models.req.LoginAccountReq
import net.yan100.compose.rds.models.req.ModifyAccountPasswordReq
import net.yan100.compose.rds.models.req.RegisterAccountReq
import java.time.LocalDateTime

interface AccountAggregator {

  /**
   * 注册账号
   */
  fun registerAccount(param: RegisterAccountReq): User?

  /**
   * 登录指定账号，返回用户信息
   */
  fun login(param: LoginAccountReq): User?

  /**
   * 根据账号修改密码
   */
  fun modifyPassword(param: ModifyAccountPasswordReq): Boolean

  /**
   * 根据账号校验密码正确性
   */
  fun verifyPassword(account: String, password: String): Boolean

  /**
   * 封禁账号到指定时间
   */
  fun bannedAccountTo(account: String, dateTime: LocalDateTime)
}
