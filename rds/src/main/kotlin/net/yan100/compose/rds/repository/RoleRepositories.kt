package net.yan100.compose.rds.repository

import net.yan100.compose.rds.base.BaseRepository
import net.yan100.compose.rds.entity.AllRoleEntity
import net.yan100.compose.rds.entity.RoleEntity
import net.yan100.compose.rds.entity.UserGroupEntity
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface RoleRepository : BaseRepository<RoleEntity> {
  fun findAllByName(name: String): List<RoleEntity>

  @Query(
    """
    from RoleEntity r
    left join RoleGroupRoleEntity rgr on r.id = rgr.roleId
    left join UserRoleGroupEntity urg on rgr.roleGroupId = urg.roleGroupId
    where urg.userId = :userId
  """
  )
  fun findAllByUserId(userId: Long): List<RoleEntity>
}

@Repository
interface AllRoleEntityRepository : BaseRepository<AllRoleEntity> {
  fun findAllByName(name: String): List<AllRoleEntity>

  @Query(
    """
    from AllRoleEntity r
    left join RoleGroupRoleEntity rgr on r.id = rgr.roleId
    left join UserRoleGroupEntity urg on rgr.roleGroupId = urg.roleGroupId
    where urg.userId = :userId
  """
  )
  fun findAllByUserId(userId: Long): List<AllRoleEntity>
}

@Repository
interface UserGroupRepository : BaseRepository<UserGroupEntity> {
  @Query(
    """
    from UserGroupEntity ug
    left join UserGroupUserEntity ugu
    on ug.id = ugu.userGroupId
    where ug.userId = :userId
    or ugu.userId = :userId
  """
  )
  fun findAllByUserId(userId: String): MutableList<UserGroupEntity>

  @Query(
    """
    from UserGroupEntity ug
    left join UserGroupUserEntity ugu on ug.id = ugu.userGroupId
    left join UserEntity u on u.id = ugu.userId
    where u.account = :account
  """
  )
  fun findAllByUserAccount(account: String): MutableList<UserGroupEntity>


  fun existsByIdAndUserId(id: String, userId: String): Boolean
}
