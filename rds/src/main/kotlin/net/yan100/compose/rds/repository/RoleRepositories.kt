package net.yan100.compose.rds.repository

import net.yan100.compose.rds.base.BaseRepository
import net.yan100.compose.rds.entity.FullRole
import net.yan100.compose.rds.entity.Role
import net.yan100.compose.rds.entity.UserGroup
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface RoleRepository : BaseRepository<Role> {
  fun findAllByName(name: String): List<Role>

  @Query(
    """
    from Role r
    left join RoleGroupRole rgr on r.id = rgr.roleId
    left join UserRoleGroup urg on rgr.roleGroupId = urg.roleGroupId
    where urg.userId = :userId
  """
  )
  fun findAllByUserId(userId: Long): List<Role>
}

@Repository
interface AllRoleEntityRepository : BaseRepository<FullRole> {
  fun findAllByName(name: String): List<FullRole>

  @Query(
    """
    from FullRole r
    left join RoleGroupRole rgr on r.id = rgr.roleId
    left join UserRoleGroup urg on rgr.roleGroupId = urg.roleGroupId
    where urg.userId = :userId
  """
  )
  fun findAllByUserId(userId: Long): List<FullRole>
}

@Repository
interface UserGroupRepository : BaseRepository<UserGroup> {
  @Query(
    """
    from UserGroup ug
    left join UserGroupUser ugu
    on ug.id = ugu.userGroupId
    where ug.userId = :userId
    or ugu.userId = :userId
  """
  )
  fun findAllByUserId(userId: String): MutableList<UserGroup>

  @Query(
    """
    from UserGroup ug
    left join UserGroupUser ugu on ug.id = ugu.userGroupId
    left join User u on u.id = ugu.userId
    where u.account = :account
  """
  )
  fun findAllByUserAccount(account: String): MutableList<UserGroup>


  fun existsByIdAndUserId(id: String, userId: String): Boolean
}
