package net.yan100.compose.rds.repositories.relationship

import net.yan100.compose.rds.entities.relationship.UserRoleGroup
import net.yan100.compose.rds.repositories.base.IRepo
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface IUserRoleGroupRepo : IRepo<UserRoleGroup> {
  fun findByUserIdAndRoleGroupId(userId: String, roleGroupId: String): UserRoleGroup?
  fun findAllByUserId(userId: String): List<UserRoleGroup>

  @Query(
    """
    SELECT ur.roleGroupId 
    FROM UserRoleGroup ur
    WHERE ur.userId = :userId
  """
  )
  fun findAllRoleGroupIdByUserId(userId: String): Set<String>

  fun existsByUserIdAndRoleGroupId(userId: String, roleId: String): Boolean
  fun deleteAllByRoleGroupIdAndUserId(roleGroupId: String, userId: String)

  fun deleteAllByRoleGroupIdInAndUserId(roleGroupIds: List<String>, userId: String)

  fun deleteAllByUserId(userId: String)
}
