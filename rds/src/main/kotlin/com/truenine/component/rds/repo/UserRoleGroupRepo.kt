package com.truenine.component.rds.repo

import com.truenine.component.rds.base.BaseRepo
import com.truenine.component.rds.entity.UserRoleGroupEntity
import org.springframework.stereotype.Repository

@Repository
interface UserRoleGroupRepo : BaseRepo<UserRoleGroupEntity, String> {
  fun findAllByUserId(userId: String): List<UserRoleGroupEntity>
  fun existsByUserIdAndRoleGroupId(userId: String, roleId: String): Boolean
  fun deleteByUserIdAndRoleGroupId(userId: String, roleId: String)
  fun deleteAllByUserId(userId: String)
}
