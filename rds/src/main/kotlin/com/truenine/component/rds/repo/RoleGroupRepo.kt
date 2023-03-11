package com.truenine.component.rds.repo

import com.truenine.component.rds.base.BaseRepo
import com.truenine.component.rds.dao.RoleGroupDao
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface RoleGroupRepo : BaseRepo<RoleGroupDao, String> {
  fun findAllByName(name: String): List<RoleGroupDao>

  @Query(
    """
    from RoleGroupDao rg
    left join UserRoleGroupDao ur on rg.id = ur.roleGroupId
    where ur.userId = :userId
  """
  )
  fun findAllByUserId(userId: String): List<RoleGroupDao>
}
