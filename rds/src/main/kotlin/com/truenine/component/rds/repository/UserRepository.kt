package com.truenine.component.rds.repository


import com.truenine.component.rds.base.BaseRepository
import com.truenine.component.rds.entity.UserEntity
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
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

  @Query("update UserEntity u set u.banTime = :banTime where u.account = :account")
  @Modifying
  fun saveUserBanTimeByAccount(banTime: LocalDateTime?, account: String)
}
