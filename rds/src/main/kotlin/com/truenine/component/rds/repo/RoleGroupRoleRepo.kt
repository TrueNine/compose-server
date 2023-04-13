package com.truenine.component.rds.repo

import com.truenine.component.rds.base.BaseRepo
import com.truenine.component.rds.entity.RoleGroupRoleEntity
import org.springframework.stereotype.Repository

@Repository
interface RoleGroupRoleRepo : BaseRepo<RoleGroupRoleEntity> {
  fun findByRoleGroupIdAndRoleId(roleGroupId: Long, roleId: Long): RoleGroupRoleEntity?
  fun findAllByRoleGroupId(roleGroupId: Long): List<RoleGroupRoleEntity>
  fun existsByRoleGroupIdAndRoleId(roleGroupId: Long, roleId: Long): Boolean
  fun deleteByRoleGroupIdAndRoleId(roleGroupId: Long, roleId: Long)
}
