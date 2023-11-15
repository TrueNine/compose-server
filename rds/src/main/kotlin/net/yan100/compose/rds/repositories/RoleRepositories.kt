package net.yan100.compose.rds.repositories

import net.yan100.compose.rds.entities.FullRole
import net.yan100.compose.rds.entities.Role
import net.yan100.compose.rds.repositories.base.IRepo
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface RoleRepository : IRepo<Role> {
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
interface AllRoleEntityRepository : IRepo<FullRole> {
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
