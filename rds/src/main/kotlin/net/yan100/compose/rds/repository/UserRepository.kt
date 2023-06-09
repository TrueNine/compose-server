package net.yan100.compose.rds.repository


import net.yan100.compose.rds.base.BaseRepository
import net.yan100.compose.rds.entity.FullUser
import net.yan100.compose.rds.entity.User
import net.yan100.compose.rds.entity.UserInfo
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Repository
interface UserRepository : BaseRepository<User> {
  fun findByAccount(account: String): User?

  @Query(
    """
    select u.id
    from User u
    where u.account = :account
  """
  )
  fun findIdByAccount(account: String): String

  @Query(
    """
    select pwdEnc
    from User
    where account = :account
  """
  )
  fun findPwdEncByAccount(account: String): String?

  fun findAllByNickName(nickName: String): List<User>

  @Query(
    """
    select r.name
    from User u
    left join UserRoleGroup urg on urg.userId = u.id
    left join RoleGroup rg on rg.id = urg.roleGroupId
    left join RoleGroupRole rgr on rgr.roleGroupId = rg.id
    left join Role r on r.id = rgr.roleId
    where u.account = :account
  """
  )
  fun findAllRoleNameByAccount(account: String): Set<String>

  @Query(
    """
    select p.name
    from User u
    left join UserRoleGroup urg on urg.userId = u.id
    left join RoleGroup rg on rg.id = urg.roleGroupId
    left join RoleGroupRole rgr on rgr.roleGroupId = rg.id
    left join Role r on r.id = rgr.roleId
    left join RolePermissions rp on rp.roleId = r.id
    left join Permissions p on p.id = rp.permissionsId
    where u.account = :account
  """
  )
  fun findAllPermissionsNameByAccount(account: String): Set<String>

  fun existsAllByAccount(account: String): Boolean

  @Modifying
  @Query("update User u set u.banTime = :banTime where u.account = :account")
  fun saveUserBanTimeByAccount(banTime: LocalDateTime?, account: String)
}

@Repository
interface FullUserRepository : BaseRepository<FullUser> {
  fun findByAccount(account: String): FullUser?
}


@Repository
interface UserInfoRepository : BaseRepository<UserInfo> {
  fun findByUserId(userId: String): UserInfo?

  /**
   * 根据 微信 openId 查询对应 User
   */
  @Query(
    """
  from UserInfo i
  left join User u on i.userId = u.id
  where i.wechatOpenId = :openId
    """
  )
  fun findUserByWechatOpenId(openId: String): User?

  /**
   * 根据 电话号码查询用户手机号
   */
  @Query(
    """
    from UserInfo i
    left join User u on i.userId = u.id
    where i.phone = :phone
  """
  )
  fun findUserByPhone(phone: String): User?

  fun existsByPhone(phone: String): Boolean

  fun existsByWechatOpenId(wechatOpenId: String): Boolean

  @Transactional(rollbackFor = [Exception::class])
  fun deleteByPhone(phone: String): Int
}
