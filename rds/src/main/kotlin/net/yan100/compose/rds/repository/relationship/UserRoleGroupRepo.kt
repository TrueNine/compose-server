package net.yan100.compose.rds.repository.relationship

import net.yan100.compose.rds.base.BaseRepository
import net.yan100.compose.rds.entity.relationship.UserRoleGroup
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface UserRoleGroupRepo : BaseRepository<UserRoleGroup> {
  fun findByUserIdAndRoleGroupId(userId: String, roleGroupId: String): UserRoleGroup?
  fun findAllByUserId(userId: String): List<UserRoleGroup>

  @Query("select ur.roleGroupId from UserRoleGroup ur")
  fun findAllRoleGroupIdByUserId(userID: String): Set<String>

  fun existsByUserIdAndRoleGroupId(userId: String, roleId: String): Boolean
  fun deleteAllByRoleGroupIdAndUserId(roleGroupId: String, userId: String)

  fun deleteAllByRoleGroupIdInAndUserId(roleGroupIds: List<String>, userId: String)

  fun deleteAllByUserId(userId: String)
}