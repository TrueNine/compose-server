package com.truenine.component.rds.repo

import com.truenine.component.rds.base.BaseRepo
import com.truenine.component.rds.entity.RolePermissionsEntity
import org.springframework.stereotype.Repository

@Repository
interface RolePermissionsRepo : BaseRepo<RolePermissionsEntity> {
  fun findByRoleIdAndPermissionsId(roleId: Long, permissionsId: Long): RolePermissionsEntity?
  fun findAllByRoleId(role: Long): List<RolePermissionsEntity>
  fun existsByRoleIdAndPermissionsId(roleId: Long, permissionsId: Long): Boolean
  fun deleteByRoleIdAndPermissionsId(roleId: Long, permissionsId: Long)
}
