package com.truenine.component.rds.repo

import com.truenine.component.rds.base.BaseRepo
import com.truenine.component.rds.dao.RolePermissionsDao
import org.springframework.stereotype.Repository

@Repository
interface RolePermissionsRepo : BaseRepo<RolePermissionsDao, String> {
  fun findAllByRoleId(role: String): List<RolePermissionsDao>
  fun existsByRoleIdAndPermissionsId(
    roleId: String,
    permissionsId: String
  ): Boolean

  fun deleteByRoleIdAndPermissionsId(roleId: String, permissionsId: String)
}
