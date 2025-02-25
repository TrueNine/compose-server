package net.yan100.compose.rds.crud.repositories.jpa

import net.yan100.compose.core.RefId
import net.yan100.compose.rds.core.IRepo
import net.yan100.compose.rds.crud.entities.jpa.RolePermissions
import org.springframework.context.annotation.Primary
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Primary
@Repository
interface IRolePermissionsRepo : IRepo<RolePermissions> {
  fun findByRoleIdAndPermissionsId(
    roleId: RefId,
    permissionsId: RefId,
  ): RolePermissions?

  @Query("select rp.permissionsId from RolePermissions rp")
  fun findAllPermissionsIdByRoleId(roleId: RefId): Set<RefId>

  fun findAllByRoleId(roleId: RefId): List<RolePermissions>

  fun existsByRoleIdAndPermissionsId(
    roleId: RefId,
    permissionsId: RefId,
  ): Boolean

  fun deleteByRoleIdAndPermissionsId(roleId: RefId, permissionsId: RefId)

  fun deleteAllByPermissionsIdInAndRoleId(
    permissionsIds: List<RefId>,
    roleId: RefId,
  )
}
