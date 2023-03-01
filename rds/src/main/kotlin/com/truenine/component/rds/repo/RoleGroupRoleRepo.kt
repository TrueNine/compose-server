package com.truenine.component.rds.repo

import com.truenine.component.rds.base.BaseRepo
import com.truenine.component.rds.dao.RoleGroupRoleDao
import org.springframework.stereotype.Repository

@Repository
interface RoleGroupRoleRepo : BaseRepo<RoleGroupRoleDao, String> {
  fun findAllByRoleGroupId(roleGroupId: String): List<com.truenine.component.rds.dao.RoleGroupRoleDao>
  fun existsByRoleGroupIdAndRoleId(roleGroupId: String, roleId: String): Boolean
  fun deleteByRoleGroupIdAndRoleId(roleGroupId: String, roleId: String)
}
