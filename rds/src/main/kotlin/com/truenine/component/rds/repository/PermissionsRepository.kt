package com.truenine.component.rds.repository

import com.truenine.component.rds.base.BaseRepository
import com.truenine.component.rds.entity.PermissionsEntity
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface PermissionsRepository : BaseRepository<PermissionsEntity> {
  fun findAllByName(name: String): List<PermissionsEntity>

  @Query(
    """
    from PermissionsEntity p
    left join RolePermissionsEntity rp on p.id = rp.permissionsId
    left join RoleGroupRoleEntity rgr on rp.roleId = rgr.roleId
    left join UserRoleGroupEntity urg on rgr.roleGroupId = urg.roleGroupId
    where urg.userId = :userId
  """
  )
  fun findAllByUserId(userId: Long): List<PermissionsEntity>
}