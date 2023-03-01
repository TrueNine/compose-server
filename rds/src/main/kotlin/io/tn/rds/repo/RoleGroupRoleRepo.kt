package io.tn.rds.repo

import io.tn.rds.base.BaseRepo
import io.tn.rds.dao.RoleGroupRoleDao
import org.springframework.stereotype.Repository

@Repository
interface RoleGroupRoleRepo : BaseRepo<RoleGroupRoleDao, String> {
  fun findAllByRoleGroupId(roleGroupId: String): List<io.tn.rds.dao.RoleGroupRoleDao>
  fun existsByRoleGroupIdAndRoleId(roleGroupId: String, roleId: String): Boolean
  fun deleteByRoleGroupIdAndRoleId(roleGroupId: String, roleId: String)
}
