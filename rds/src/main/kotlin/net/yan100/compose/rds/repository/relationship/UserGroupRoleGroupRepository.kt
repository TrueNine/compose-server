package net.yan100.compose.rds.repository.relationship

import net.yan100.compose.rds.base.BaseRepository
import net.yan100.compose.rds.entity.relationship.UserGroupRoleGroup
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface UserGroupRoleGroupRepository : BaseRepository<UserGroupRoleGroup> {
  fun findByUserGroupIdAndRoleGroupId(userGroupId: String, roleGroupId: String): UserGroupRoleGroup?

  @Query("select ur.roleGroupId from UserGroupRoleGroup ur")
  fun findAllRoleGroupIdByUserGroupId(userGroupId: String): Set<String>
  fun findAllByUserGroupId(userGroupId: String): List<UserGroupRoleGroup>
  fun existsByUserGroupIdAndRoleGroupId(
    userGroupId: String,
    roleGroupId: String
  ): Boolean

  fun deleteByUserGroupIdAndRoleGroupId(
    userGroupId: String,
    roleGroupId: String
  )

  fun deleteAllByUserGroupIdAndRoleGroupId(userGroupId: String, roleGroupId: String)

  fun deleteAllByRoleGroupIdInAndUserGroupId(roleGroupIds: List<String>, userGroupId: String)

  fun deleteAllByUserGroupId(userGroupId: String)
}
