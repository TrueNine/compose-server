package net.yan100.compose.rds.repository.relationship

import net.yan100.compose.rds.base.BaseRepository
import net.yan100.compose.rds.entity.relationship.UserRoleGroupEntity
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface UserRoleGroupRepository : BaseRepository<UserRoleGroupEntity> {
  fun findByUserIdAndRoleGroupId(userId: String, roleGroupId: String): UserRoleGroupEntity?
  fun findAllByUserId(userId: String): List<UserRoleGroupEntity>

  @Query("select ur.roleGroupId from UserRoleGroupEntity ur")
  fun findAllRoleGroupIdByUserId(userID: String): Set<String>

  fun existsByUserIdAndRoleGroupId(userId: String, roleId: String): Boolean
  fun deleteAllByRoleGroupIdAndUserId(roleGroupId: String, userId: String)

  fun deleteAllByRoleGroupIdInAndUserId(roleGroupIds: List<String>, userId: String)

  fun deleteAllByUserId(userId: String)
}
