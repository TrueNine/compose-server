package com.truenine.component.rds.repository

import com.truenine.component.rds.base.BaseRepository
import com.truenine.component.rds.entity.RoleEntity
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface RoleRepository : BaseRepository<RoleEntity> {
  fun findAllByName(name: String): List<RoleEntity>

  @Query(
    """
    from RoleEntity r
    left join RoleGroupRoleEntity rgr on r.id = rgr.roleId
    left join UserRoleGroupEntity urg on rgr.roleGroupId = urg.roleGroupId
    where urg.userId = :userId
  """
  )
  fun findAllByUserId(userId: Long): List<RoleEntity>
}
