package net.yan100.compose.rds.repositories.relationship

import net.yan100.compose.rds.entities.relationship.RoleGroupRole
import net.yan100.compose.rds.repositories.base.IRepo
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface IRoleGroupRoleRepo :
  IRepo<RoleGroupRole> {
  fun findByRoleGroupIdAndRoleId(roleGroupId: String, roleId: String): RoleGroupRole?

  @Query("select rr.roleId from RoleGroupRole rr")
  fun findAllRoleIdByRoleGroupId(roleGroupId: String): Set<String>
  fun findAllByRoleGroupId(roleGroupId: String): List<RoleGroupRole>
  fun existsByRoleGroupIdAndRoleId(roleGroupId: String, roleId: String): Boolean
  fun deleteByRoleGroupIdAndRoleId(roleGroupId: String, roleId: String)
  fun deleteAllByRoleIdInAndRoleGroupId(roleIds: List<String>, roleGroupId: String)
}
