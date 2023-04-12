package com.truenine.component.rds.repo

import com.truenine.component.rds.base.BaseRepo
import com.truenine.component.rds.entity.RoleGroupRoleEntity
import org.springframework.stereotype.Repository

@Repository
interface RoleGroupRoleRepo : BaseRepo<RoleGroupRoleEntity, String> {
  fun findByRoleGroupIdAndRoleId(roleGroupId: String, roleId: String): RoleGroupRoleEntity?
  fun findAllByRoleGroupId(roleGroupId: String): List<RoleGroupRoleEntity>
  fun existsByRoleGroupIdAndRoleId(roleGroupId: String, roleId: String): Boolean
  fun deleteByRoleGroupIdAndRoleId(roleGroupId: String, roleId: String)
}
