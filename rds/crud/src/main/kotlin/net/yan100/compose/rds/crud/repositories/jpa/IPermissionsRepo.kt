package net.yan100.compose.rds.crud.repositories.jpa

import net.yan100.compose.rds.IRepo
import net.yan100.compose.rds.crud.entities.jpa.Permissions
import org.springframework.context.annotation.Primary
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Primary
@Deprecated("弃用 JPA")
@Repository("IPermissionsRepository")
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
