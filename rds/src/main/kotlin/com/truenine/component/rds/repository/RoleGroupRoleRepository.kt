package com.truenine.component.rds.repository

import com.truenine.component.rds.base.BaseRepository
import com.truenine.component.rds.entity.RoleGroupRoleEntity
import org.springframework.stereotype.Repository

@Repository
interface RoleGroupRoleRepository : BaseRepository<RoleGroupRoleEntity> {
  fun findByRoleGroupIdAndRoleId(roleGroupId: Long, roleId: Long): RoleGroupRoleEntity?
  fun findAllByRoleGroupId(roleGroupId: Long): List<RoleGroupRoleEntity>
  fun existsByRoleGroupIdAndRoleId(roleGroupId: Long, roleId: Long): Boolean
  fun deleteByRoleGroupIdAndRoleId(roleGroupId: Long, roleId: Long)
  fun deleteAllByRoleIdInAndRoleGroupId(roleIds: List<Long>, roleGroupId: Long)
}
