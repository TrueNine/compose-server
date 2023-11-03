package net.yan100.compose.rds.repository.relationship

import net.yan100.compose.rds.entity.relationship.RolePermissions
import net.yan100.compose.rds.repository.base.IRepo
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface RolePermissionsRepo : IRepo<RolePermissions> {
  fun findByRoleIdAndPermissionsId(roleId: String, permissionsId: String): RolePermissions?

  @Query("select rp.permissionsId from RolePermissions rp")
  fun findAllPermissionsIdByRoleId(roleId: String): Set<String>
  fun findAllByRoleId(role: String): List<RolePermissions>
  fun existsByRoleIdAndPermissionsId(roleId: String, permissionsId: String): Boolean
  fun deleteByRoleIdAndPermissionsId(roleId: String, permissionsId: String)
  fun deleteAllByPermissionsIdInAndRoleId(permissionsIds: List<String>, roleId: String)
}
