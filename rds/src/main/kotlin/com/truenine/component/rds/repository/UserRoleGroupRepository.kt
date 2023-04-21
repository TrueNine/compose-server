package com.truenine.component.rds.repository

import com.truenine.component.rds.base.BaseRepository
import com.truenine.component.rds.entity.UserRoleGroupEntity
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface UserRoleGroupRepository : BaseRepository<UserRoleGroupEntity> {
  fun findByUserIdAndRoleGroupId(userId: Long, roleGroupId: Long): UserRoleGroupEntity?
  fun findAllByUserId(userId: Long): List<UserRoleGroupEntity>
  @Query("select ur.roleGroupId from UserRoleGroupEntity ur")
  fun findAllRoleGroupIdByUserId(userID: Long): Set<Long>
  fun existsByUserIdAndRoleGroupId(userId: Long, roleId: Long): Boolean
  fun deleteAllByRoleGroupIdAndUserId(roleGroupId: Long, userId: Long)

  fun deleteAllByRoleGroupIdInAndUserId(roleGroupIds: List<Long>, userId: Long)

  fun deleteAllByUserId(userId: Long)
}
