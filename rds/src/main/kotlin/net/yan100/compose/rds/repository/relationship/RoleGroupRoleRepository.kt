package net.yan100.compose.rds.repository.relationship

import net.yan100.compose.rds.base.BaseRepository
import net.yan100.compose.rds.entity.relationship.RoleGroupRoleEntity
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface RoleGroupRoleRepository : BaseRepository<RoleGroupRoleEntity> {
  fun findByRoleGroupIdAndRoleId(roleGroupId: String, roleId: String): RoleGroupRoleEntity?

  @Query("select rr.roleId from RoleGroupRoleEntity rr")
  fun findAllRoleIdByRoleGroupId(roleGroupId: String): Set<String>
  fun findAllByRoleGroupId(roleGroupId: String): List<RoleGroupRoleEntity>
  fun existsByRoleGroupIdAndRoleId(roleGroupId: String, roleId: String): Boolean
  fun deleteByRoleGroupIdAndRoleId(roleGroupId: String, roleId: String)
  fun deleteAllByRoleIdInAndRoleGroupId(roleIds: List<String>, roleGroupId: String)
}
