package net.yan100.compose.rds.repository


import net.yan100.compose.rds.entity.FullUser
import net.yan100.compose.rds.entity.User
import net.yan100.compose.rds.entity.UserInfo
import net.yan100.compose.rds.repository.base.IRepo
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Repository
interface UserRepo : IRepo<User> {
  fun findByAccount(account: String): User?

  @Query(
    """
    SELECT u.id
    FROM User u
    WHERE u.account = :account
  """
  )
  fun findIdByAccount(account: String): String

  @Query(
    """
    SELECT pwdEnc
    FROM User
    WHERE account = :account
  """
  )
  fun findPwdEncByAccount(account: String): String?

  fun findAllByNickName(nickName: String): List<User>

  @Query(
    """
    SELECT r.name
    FROM User u
    LEFT JOIN UserRoleGroup urg ON urg.userId = u.id
    LEFT JOIN RoleGroup rg ON rg.id = urg.roleGroupId
    LEFT JOIN RoleGroupRole rgr ON rgr.roleGroupId = rg.id
    LEFT JOIN Role r ON r.id = rgr.roleId
    WHERE u.account = :account
  """
  )
  fun findAllRoleNameByAccount(account: String): Set<String>

  @Query(
    """
    SELECT p.name
    FROM User u
    LEFT JOIN UserRoleGroup urg ON urg.userId = u.id
    LEFT JOIN RoleGroup rg ON rg.id = urg.roleGroupId
    LEFT JOIN RoleGroupRole rgr ON rgr.roleGroupId = rg.id
    LEFT JOIN Role r ON r.id = rgr.roleId
    LEFT JOIN RolePermissions rp ON rp.roleId = r.id
    LEFT JOIN Permissions p ON p.id = rp.permissionsId
    WHERE u.account = :account
  """
  )
  fun findAllPermissionsNameByAccount(account: String): Set<String>

  fun existsAllByAccount(account: String): Boolean

  @Modifying
  @Query("UPDATE User u SET u.banTime = :banTime WHERE u.account = :account")
  fun saveUserBanTimeByAccount(banTime: LocalDateTime?, account: String)

  @Query(
    """
    SELECT count(i.id) > 0
    FROM UserInfo i
    LEFT JOIN User u ON i.userId = u.id
    WHERE i.wechatOpenId = :openId
  """
  )
  fun existsByWechatOpenId(openId: String): Boolean
}

@Repository
interface FullUserRepository : IRepo<FullUser> {
  fun findByAccount(account: String): FullUser?
}


@Repository
interface UserInfoRepo : IRepo<UserInfo> {
  fun findByUserId(userId: String): UserInfo?

  /**
   * 根据 微信 openId 查询对应 User
   */
  @Query(
    """
    FROM User u
    LEFT JOIN UserInfo i ON u.id = i.userId
    WHERE i.wechatOpenId = :openId
    """
  )
  fun findUserByWechatOpenId(openId: String): User?

  /**
   * 根据 电话号码查询用户手机号
   */
  @Query(
    """
    FROM User u
    LEFT JOIN UserInfo i ON u.id = i.userId
    WHERE i.phone = :phone
  """
  )
  fun findUserByPhone(phone: String): User?

  fun existsByPhone(phone: String): Boolean

  fun existsByWechatOpenId(wechatOpenId: String): Boolean

  @Transactional(rollbackFor = [Exception::class])
  fun deleteByPhone(phone: String): Int
}
