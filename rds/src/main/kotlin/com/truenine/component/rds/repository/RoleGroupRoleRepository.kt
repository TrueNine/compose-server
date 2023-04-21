package com.truenine.component.rds.repository

import com.truenine.component.rds.base.BaseRepository
import com.truenine.component.rds.entity.relationship.RoleGroupRoleEntity
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface RoleGroupRoleRepository : BaseRepository<RoleGroupRoleEntity> {
  fun findByRoleGroupIdAndRoleId(roleGroupId: Long, roleId: Long): RoleGroupRoleEntity?
  @Query("select rr.roleId from RoleGroupRoleEntity rr")
  fun findAllRoleIdByRoleGroupId(roleGroupId: Long): Set<Long>
  fun findAllByRoleGroupId(roleGroupId: Long): List<RoleGroupRoleEntity>
  fun existsByRoleGroupIdAndRoleId(roleGroupId: Long, roleId: Long): Boolean
  fun deleteByRoleGroupIdAndRoleId(roleGroupId: Long, roleId: Long)
  fun deleteAllByRoleIdInAndRoleGroupId(roleIds: List<Long>, roleGroupId: Long)
}
