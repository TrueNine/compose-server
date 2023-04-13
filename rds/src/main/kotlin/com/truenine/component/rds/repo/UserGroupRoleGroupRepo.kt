package com.truenine.component.rds.repo

import com.truenine.component.rds.base.BaseRepo
import com.truenine.component.rds.entity.UserGroupRoleGroupEntity
import org.springframework.stereotype.Repository

@Repository
interface UserGroupRoleGroupRepo : BaseRepo<UserGroupRoleGroupEntity> {
  fun findByUserGroupIdAndRoleGroupId(userGroupId: Long, roleGroupId: Long): UserGroupRoleGroupEntity?
  fun findAllByUserGroupId(userGroupId: Long): List<UserGroupRoleGroupEntity>
  fun existsByUserGroupIdAndRoleGroupId(
    userGroupId: Long,
    roleGroupId: Long
  ): Boolean

  fun deleteByUserGroupIdAndRoleGroupId(
    userGroupId: Long,
    roleGroupId: Long
  )

  fun deleteAllByUserGroupId(userGroupId: Long)
}
