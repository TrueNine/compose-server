package io.tn.rds.repo

import io.tn.rds.base.BaseRepo
import io.tn.rds.dao.RolePermissionsDao
import org.springframework.stereotype.Repository

@Repository
interface RolePermissionsRepo : BaseRepo<RolePermissionsDao, String> {
  fun findAllByRoleId(role: String): List<io.tn.rds.dao.RolePermissionsDao>
  fun existsByRoleIdAndPermissionsId(
    roleId: String,
    permissionsId: String
  ): Boolean

  fun deleteByRoleIdAndPermissionsId(roleId: String, permissionsId: String)
}
