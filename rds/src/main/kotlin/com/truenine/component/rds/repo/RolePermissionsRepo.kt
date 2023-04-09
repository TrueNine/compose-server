package com.truenine.component.rds.repo

import com.truenine.component.rds.base.BaseRepo
import com.truenine.component.rds.entity.RolePermissionsEntity
import org.springframework.stereotype.Repository

@Repository
interface RolePermissionsRepo : BaseRepo<RolePermissionsEntity, String> {
  fun findAllByRoleId(role: String): List<RolePermissionsEntity>
  fun existsByRoleIdAndPermissionsId(
    roleId: String,
    permissionsId: String
  ): Boolean

  fun deleteByRoleIdAndPermissionsId(roleId: String, permissionsId: String)
}
