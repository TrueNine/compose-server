package net.yan100.compose.rds.repository.relationship

import net.yan100.compose.rds.base.BaseRepository
import net.yan100.compose.rds.entity.relationship.RolePermissionsEntity
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface RolePermissionsRepository : BaseRepository<RolePermissionsEntity> {
  fun findByRoleIdAndPermissionsId(roleId: String, permissionsId: String): RolePermissionsEntity?

  @Query("select rp.permissionsId from RolePermissionsEntity rp")
  fun findAllPermissionsIdByRoleId(roleId: String): Set<String>
  fun findAllByRoleId(role: String): List<RolePermissionsEntity>
  fun existsByRoleIdAndPermissionsId(roleId: String, permissionsId: String): Boolean
  fun deleteByRoleIdAndPermissionsId(roleId: String, permissionsId: String)
  fun deleteAllByPermissionsIdInAndRoleId(permissionsIds: List<String>, roleId: String)
}
