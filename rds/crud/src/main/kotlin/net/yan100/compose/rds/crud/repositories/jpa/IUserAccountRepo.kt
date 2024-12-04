package net.yan100.compose.rds.crud.repositories.jpa

import net.yan100.compose.core.RefId
import net.yan100.compose.rds.core.IRepo
import net.yan100.compose.rds.crud.entities.jpa.UserAccount
import org.springframework.context.annotation.Primary
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Primary
@Repository
interface IUserAccountRepo : IRepo<UserAccount> {

  @Query(
    """
    select count(u.id) > 0
    from UserAccount u
    left join UserInfo i on i.userId = u.id
    where i.id = :userInfoId
  """
  )
  fun existsByUserInfoId(userInfoId: RefId): Boolean

  fun findByAccount(account: String): UserAccount?

  @Query(
    """
    select u.id
    from UserAccount u
    where u.account = :account
  """
  )
  fun findIdByAccount(account: String): String

  @Query(
    """
    select pwdEnc
    from UserAccount
    where account = :account
  """
  )
  fun findPwdEncByAccount(account: String): String?

  @Query(
    """
    from UserAccount u
    left join UserInfo i on i.userId = u.id
    where i.pri = true and i.phone = :phone
  """
  )
  fun findAccountByUserInfoPhone(phone: String): String?

  @Query(
    """
    select u.account
    from UserAccount u
    left join UserInfo i on i.userId = u.id
    where i.pri = true
    and i.wechatOpenid = :openid
  """
  )
  fun findAccountByUserInfoWechatOpenid(openid: String): String?

  fun findAllByNickName(nickName: String): List<UserAccount>

  @Query(
    """
    select r.name
    from UserAccount u
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
    from UserAccount u
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
  @Query("UPDATE UserAccount u SET u.banTime = :banTime WHERE u.account = :account")
  fun saveUserBanTimeByAccount(banTime: LocalDateTime?, account: String)

  @Query(
    """
    select count(i.id) > 0
    from UserInfo i
    left join UserAccount u on i.userId = u.id
    where i.wechatOpenid = :openId
  """
  )
  fun existsByWechatOpenId(openId: String): Boolean
}
