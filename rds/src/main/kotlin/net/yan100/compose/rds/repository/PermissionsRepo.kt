package net.yan100.compose.rds.repository

import net.yan100.compose.rds.base.BaseRepository
import net.yan100.compose.rds.entity.Permissions
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface PermissionsRepo : BaseRepository<Permissions> {
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
