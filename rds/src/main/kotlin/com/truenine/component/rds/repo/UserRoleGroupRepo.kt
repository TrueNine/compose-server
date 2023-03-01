package com.truenine.component.rds.repo

import com.truenine.component.rds.base.BaseRepo
import com.truenine.component.rds.dao.UserRoleGroupDao
import org.springframework.stereotype.Repository

@Repository
interface UserRoleGroupRepo : BaseRepo<UserRoleGroupDao, String> {
  fun findAllByUserId(userId: String): List<com.truenine.component.rds.dao.UserRoleGroupDao>
  fun existsByUserIdAndRoleGroupId(userId: String, roleId: String): Boolean
  fun deleteByUserIdAndRoleGroupId(userId: String, roleId: String)
  fun deleteAllByUserId(userId: String)
}
