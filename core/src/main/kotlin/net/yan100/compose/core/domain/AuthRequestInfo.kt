package net.yan100.compose.core.domain

import java.util.concurrent.CopyOnWriteArrayList
import net.yan100.compose.core.RefId

/**
 * security校验所需的用户身份
 *
 * @param deviceId 设备ID
 * @param currentIpAddr 当前请求的IP地址
 * @param account 账号
 * @param encryptedPassword 加密后的密码
 * @param loginIpAddr 登录IP地址
 * @param loginPlatform 登录平台
 * @param userId 用户ID
 * @param nonLocked 是否锁定
 * @param nonExpired 是否过期
 * @param enabled 是否启用
 * @param roles 角色列表
 * @param permissions 权限列表
 * @param depts 部门列表
 * @author TrueNine
 * @since 2022-12-10
 */
data class AuthRequestInfo(
  override val deviceId: String? = null,
  override val currentIpAddr: String? = null,
  val encryptedPassword: String? = null,
  val loginIpAddr: String? = null,
  val loginPlatform: String? = null,
  val userId: RefId? = null,
  val account: String? = null,
  val nonLocked: Boolean = false,
  val nonExpired: Boolean = false,
  val enabled: Boolean = false,
  val roles: List<String> = CopyOnWriteArrayList(),
  val permissions: List<String> = CopyOnWriteArrayList(),
  val depts: List<String> = CopyOnWriteArrayList(),
) : RequestInfo() {
  /** ## 当前是否已经完全登录 */
  val isLogin
    get() =
      userId != null && enabled && nonLocked && nonExpired && account != null
}
