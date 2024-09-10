package net.yan100.compose.rds.repositories.rbac

import net.yan100.compose.rds.entities.Role
import net.yan100.compose.rds.repositories.base.IRepo
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface IRoleRepo : IRepo<Role> {
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
