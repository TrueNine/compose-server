package com.truenine.component.rds.repository

import com.truenine.component.rds.base.BaseRepository
import com.truenine.component.rds.entity.UserGroupRoleGroupEntity
import org.springframework.stereotype.Repository

@Repository
interface UserGroupRoleGroupRepository : BaseRepository<UserGroupRoleGroupEntity> {
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

  fun deleteAllByUserGroupIdAndRoleGroupId(userGroupId: Long, roleGroupId: Long)

  fun deleteAllByRoleGroupIdInAndUserGroupId(roleGroupIds: List<Long>, userGroupId: Long)

  fun deleteAllByUserGroupId(userGroupId: Long)
}
