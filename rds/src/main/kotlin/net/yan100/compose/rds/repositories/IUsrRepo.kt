package net.yan100.compose.rds.repositories


import net.yan100.compose.rds.entities.FullUsr
import net.yan100.compose.rds.entities.UserInfo
import net.yan100.compose.rds.entities.Usr
import net.yan100.compose.rds.repositories.base.IRepo
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Repository
interface IUsrRepo : IRepo<Usr> {
  fun findByAccount(account: String): Usr?

  @Query(
    """
    SELECT u.id
    FROM Usr u
    WHERE u.account = :account
  """
  )
  fun findIdByAccount(account: String): String

  @Query(
    """
    SELECT pwdEnc
    FROM Usr
    WHERE account = :account
  """
  )
  fun findPwdEncByAccount(account: String): String?

  @Query(
    """
    from Usr u
    left join UserInfo i on i.userId = u.id
    where i.pri = true and i.phone = :phone
  """
  )
  fun findAccountByUserInfoPhone(phone: String): String?

  @Query(
    """
    from Usr u
    left join UserInfo i on i.userId = u.id
    where i.pri = true and i.wechatOpenid = :openid
  """
  )
  fun findAccountByUserInfoWechatOpenid(openid: String): String?

  fun findAllByNickName(nickName: String): List<Usr>

  @Query(
    """
    SELECT r.name
    FROM Usr u
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
    FROM Usr u
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
  @Query("UPDATE Usr u SET u.banTime = :banTime WHERE u.account = :account")
  fun saveUserBanTimeByAccount(banTime: LocalDateTime?, account: String)

  @Query(
    """
    SELECT count(i.id) > 0
    FROM UserInfo i
    LEFT JOIN Usr u ON i.userId = u.id
    WHERE i.wechatOpenid = :openId
  """
  )
  fun existsByWechatOpenId(openId: String): Boolean
}

@Repository
interface IFullUserRepo : IRepo<FullUsr> {
  fun findByAccount(account: String): FullUsr?
}


@Repository
interface UserInfoRepo : IRepo<UserInfo> {
  fun findByUserId(userId: String): UserInfo?

  /**
   * 根据 微信 openId 查询对应 User
   */
  @Query(
    """
    FROM Usr u
    LEFT JOIN UserInfo i ON u.id = i.userId
    WHERE i.wechatOpenid = :openid
    """
  )
  fun findUserByWechatOpenId(openid: String): Usr?

  /**
   * 根据 电话号码查询用户手机号
   */
  @Query(
    """
    FROM Usr u
    LEFT JOIN UserInfo i ON u.id = i.userId
    WHERE i.phone = :phone
  """
  )
  fun findUserByPhone(phone: String): Usr?

  fun existsByPhone(phone: String): Boolean

  fun existsByWechatOpenid(wechatOpenId: String): Boolean

  @Transactional(rollbackFor = [Exception::class])
  fun deleteByPhone(phone: String): Int
}
