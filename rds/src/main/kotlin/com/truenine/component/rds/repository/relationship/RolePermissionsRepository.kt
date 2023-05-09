package com.truenine.component.rds.repository.relationship

import com.truenine.component.rds.base.BaseRepository
import com.truenine.component.rds.entity.relationship.RolePermissionsEntity
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
