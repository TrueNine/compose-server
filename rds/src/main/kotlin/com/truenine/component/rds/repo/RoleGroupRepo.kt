package com.truenine.component.rds.repo

import com.truenine.component.rds.base.BaseRepo
import com.truenine.component.rds.entity.RoleGroupEntity
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface RoleGroupRepo : BaseRepo<RoleGroupEntity> {
  fun findAllByName(name: String): List<RoleGroupEntity>

  @Query(
      """
    from RoleGroupEntity rg
    left join UserRoleGroupEntity ur on rg.id = ur.roleGroupId
    where ur.userId = :userId
  """
  )
  fun findAllByUserId(userId: Long): List<RoleGroupEntity>
}
