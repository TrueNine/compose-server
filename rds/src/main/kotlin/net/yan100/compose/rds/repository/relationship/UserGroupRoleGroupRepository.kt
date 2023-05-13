package net.yan100.compose.rds.repository.relationship

import net.yan100.compose.rds.base.BaseRepository
import net.yan100.compose.rds.entity.relationship.UserGroupRoleGroupEntity
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface UserGroupRoleGroupRepository : BaseRepository<UserGroupRoleGroupEntity> {
  fun findByUserGroupIdAndRoleGroupId(userGroupId: String, roleGroupId: String): UserGroupRoleGroupEntity?

  @Query("select ur.roleGroupId from UserGroupRoleGroupEntity ur")
  fun findAllRoleGroupIdByUserGroupId(userGroupId: String): Set<String>
  fun findAllByUserGroupId(userGroupId: String): List<UserGroupRoleGroupEntity>
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
