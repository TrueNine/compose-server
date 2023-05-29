package net.yan100.compose.rds.repository


import net.yan100.compose.rds.base.BaseRepository
import net.yan100.compose.rds.entity.FullUserEntity
import net.yan100.compose.rds.entity.UserEntity
import net.yan100.compose.rds.entity.UserInfoEntity
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Repository
interface UserRepository : BaseRepository<UserEntity> {
  fun findByAccount(account: String): UserEntity?

  @Query(
    """
    select u.id
    from UserEntity u
    where u.account = :account
  """
  )
  fun findIdByAccount(account: String): String

  @Query(
    """
    select pwdEnc
    from UserEntity
    where account = :account
  """
  )
  fun findPwdEncByAccount(account: String): String?

  fun findAllByNickName(nickName: String): List<UserEntity>

  @Query(
    """
    select r.name
    from UserEntity u
    left join UserRoleGroupEntity urg on urg.userId = u.id
    left join RoleGroupEntity rg on rg.id = urg.roleGroupId
    left join RoleGroupRoleEntity rgr on rgr.roleGroupId = rg.id
    left join RoleEntity r on r.id = rgr.roleId
    where u.account = :account
  """
  )
  fun findAllRoleNameByAccount(account: String): Set<String>

  @Query(
    """
    select p.name
    from UserEntity u
    left join UserRoleGroupEntity urg on urg.userId = u.id
    left join RoleGroupEntity rg on rg.id = urg.roleGroupId
    left join RoleGroupRoleEntity rgr on rgr.roleGroupId = rg.id
    left join RoleEntity r on r.id = rgr.roleId
    left join RolePermissionsEntity rp on rp.roleId = r.id
    left join PermissionsEntity p on p.id = rp.permissionsId
    where u.account = :account
  """
  )
  fun findAllPermissionsNameByAccount(account: String): Set<String>

  fun existsAllByAccount(account: String): Boolean

  @Modifying
  @Query("update UserEntity u set u.banTime = :banTime where u.account = :account")
  fun saveUserBanTimeByAccount(banTime: LocalDateTime?, account: String)
}

@Repository
interface FullUserRepository : BaseRepository<FullUserEntity> {
  fun findByAccount(account: String): FullUserEntity?
}


@Repository
interface UserInfoRepository : BaseRepository<UserInfoEntity> {
  fun findByUserId(userId: String): UserInfoEntity?

  /**
   * 根据 微信 openId 查询对应 User
   */
  @Query(
    """
  from UserInfoEntity i
  left join UserEntity u on i.userId = u.id
  where i.wechatOpenId = :openId
    """
  )
  fun findUserByWechatOpenId(openId: String): UserEntity?

  /**
   * 根据 电话号码查询用户手机号
   */
  @Query(
    """
    from UserInfoEntity i
    left join UserEntity u on i.userId = u.id
    where i.phone = :phone
  """
  )
  fun findUserByPhone(phone: String): UserEntity?

  @Transactional(rollbackFor = [Exception::class])
  fun deleteByPhone(phone: String): Int
}
