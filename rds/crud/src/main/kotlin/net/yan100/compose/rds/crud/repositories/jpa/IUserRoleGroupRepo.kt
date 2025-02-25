package net.yan100.compose.rds.crud.repositories.jpa

import net.yan100.compose.core.RefId
import net.yan100.compose.rds.core.IRepo
import net.yan100.compose.rds.crud.entities.jpa.UserRoleGroup
import org.springframework.context.annotation.Primary
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Primary
@Repository
interface IUserRoleGroupRepo : IRepo<UserRoleGroup> {
  fun findByUserIdAndRoleGroupId(
    userId: RefId,
    roleGroupId: RefId,
  ): UserRoleGroup?

  fun findAllByUserId(userId: RefId): List<UserRoleGroup>

  @Query(
    """
    SELECT ur.roleGroupId 
    FROM UserRoleGroup ur
    WHERE ur.userId = :userId
  """
  )
  fun findAllRoleGroupIdByUserId(userId: RefId): Set<RefId>

  fun existsByUserIdAndRoleGroupId(userId: RefId, roleId: RefId): Boolean

  fun deleteAllByRoleGroupIdAndUserId(roleGroupId: RefId, userId: RefId)

  fun deleteAllByRoleGroupIdInAndUserId(
    roleGroupIds: List<RefId>,
    userId: RefId,
  )

  fun deleteAllByUserId(userId: RefId)
}
