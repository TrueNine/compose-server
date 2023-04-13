package com.truenine.component.rds.repo

import com.truenine.component.rds.base.BaseRepo
import com.truenine.component.rds.entity.UserRoleGroupEntity
import org.springframework.stereotype.Repository

@Repository
interface UserRoleGroupRepo : BaseRepo<UserRoleGroupEntity> {
  fun findByUserIdAndRoleGroupId(userId: Long, roleGroupId: Long): UserRoleGroupEntity?
  fun findAllByUserId(userId: Long): List<UserRoleGroupEntity>
  fun existsByUserIdAndRoleGroupId(userId: Long, roleId: Long): Boolean
  fun deleteByUserIdAndRoleGroupId(userId: Long, roleId: Long)
  fun deleteAllByUserId(userId: Long)
}
