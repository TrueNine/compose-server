package net.yan100.compose.rds.repository

import net.yan100.compose.rds.entity.Permissions
import net.yan100.compose.rds.repository.base.IRepo
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface PermissionsRepo : IRepo<Permissions> {
  fun findAllByName(name: String): List<Permissions>

  @Query(
    """
    FROM Permissions p
    LEFT JOIN RolePermissions rp on p.id = rp.permissionsId
    LEFT JOIN RoleGroupRole rgr on rp.roleId = rgr.roleId
    LEFT JOIN UserRoleGroup urg on rgr.roleGroupId = urg.roleGroupId
    WHERE urg.userId = :userId
  """
  )
  fun findAllByUserId(userId: String): List<Permissions>
}
