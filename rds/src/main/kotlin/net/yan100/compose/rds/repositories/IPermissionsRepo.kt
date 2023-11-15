package net.yan100.compose.rds.repositories

import net.yan100.compose.rds.entities.Permissions
import net.yan100.compose.rds.repositories.base.IRepo
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface IPermissionsRepo : IRepo<Permissions> {
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
