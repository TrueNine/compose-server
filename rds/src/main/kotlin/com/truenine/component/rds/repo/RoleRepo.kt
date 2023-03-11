package com.truenine.component.rds.repo

import com.truenine.component.rds.base.BaseRepo
import com.truenine.component.rds.dao.RoleDao
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface RoleRepo : BaseRepo<RoleDao, String> {
  fun findAllByName(name: String): List<RoleDao>

  @Query(
    """
    from RoleDao r
    left join RoleGroupRoleDao rgr on r.id = rgr.roleId
    left join UserRoleGroupDao urg on rgr.roleGroupId = urg.roleGroupId
    where urg.userId = :userId
  """
  )
  fun findAllByUserId(userId: String): List<RoleDao>
}
