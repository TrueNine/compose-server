package com.truenine.component.rds.repository

import com.truenine.component.rds.base.BaseRepository
import com.truenine.component.rds.entity.RolePermissionsEntity
import org.springframework.stereotype.Repository

@Repository
interface RolePermissionsRepository : BaseRepository<RolePermissionsEntity> {
  fun findByRoleIdAndPermissionsId(roleId: Long, permissionsId: Long): RolePermissionsEntity?
  fun findAllByRoleId(role: Long): List<RolePermissionsEntity>
  fun existsByRoleIdAndPermissionsId(roleId: Long, permissionsId: Long): Boolean
  fun deleteByRoleIdAndPermissionsId(roleId: Long, permissionsId: Long)
  fun deleteAllByPermissionsIdInAndRoleId(permissionsIds: List<Long>, roleId: Long)
}
