package io.tn.rds.repo

import io.tn.rds.base.BaseRepo
import io.tn.rds.dao.UserRoleGroupDao
import org.springframework.stereotype.Repository

@Repository
interface UserRoleGroupRepo : BaseRepo<UserRoleGroupDao, String> {
  fun findAllByUserId(userId: String): List<io.tn.rds.dao.UserRoleGroupDao>
  fun existsByUserIdAndRoleGroupId(userId: String, roleId: String): Boolean
  fun deleteByUserIdAndRoleGroupId(userId: String, roleId: String)
  fun deleteAllByUserId(userId: String)
}
