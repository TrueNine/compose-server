package com.truenine.component.rds.repo

import com.truenine.component.rds.base.BaseRepo
import com.truenine.component.rds.entity.UserGroupRoleGroupEntity
import org.springframework.stereotype.Repository

@Repository
interface UserGroupRoleGroupRepo : BaseRepo<UserGroupRoleGroupEntity, String> {
  fun findAllByUserGroupId(userGroupId: String): List<UserGroupRoleGroupEntity>
  fun existsByUserGroupIdAndRoleGroupId(
    userGroupId: String,
    roleGroupId: String
  ): Boolean

  fun deleteByUserGroupIdAndRoleGroupId(
    userGroupId: String,
    roleGroupId: String
  )

  fun deleteAllByUserGroupId(userGroupId: String)
}
