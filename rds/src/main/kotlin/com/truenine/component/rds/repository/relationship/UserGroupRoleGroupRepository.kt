package com.truenine.component.rds.repository.relationship

import com.truenine.component.rds.base.BaseRepository
import com.truenine.component.rds.entity.relationship.UserGroupRoleGroupEntity
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface UserGroupRoleGroupRepository : BaseRepository<UserGroupRoleGroupEntity> {
  fun findByUserGroupIdAndRoleGroupId(userGroupId: Long, roleGroupId: Long): UserGroupRoleGroupEntity?

  @Query("select ur.roleGroupId from UserGroupRoleGroupEntity ur")
  fun findAllRoleGroupIdByUserGroupId(userGroupId: Long): Set<Long>
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
