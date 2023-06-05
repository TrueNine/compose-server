package net.yan100.compose.rds.repository

import net.yan100.compose.rds.base.BaseRepository
import net.yan100.compose.rds.entity.Permissions
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface PermissionsRepository : BaseRepository<Permissions> {
  fun findAllByName(name: String): List<Permissions>

  @Query(
    """
    from Permissions p
    left join RolePermissions rp on p.id = rp.permissionsId
    left join RoleGroupRole rgr on rp.roleId = rgr.roleId
    left join UserRoleGroup urg on rgr.roleGroupId = urg.roleGroupId
    where urg.userId = :userId
  """
  )
  fun findAllByUserId(userId: Long): List<Permissions>
}
