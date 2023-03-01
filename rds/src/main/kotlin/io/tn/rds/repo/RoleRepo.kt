package io.tn.rds.repo

import io.tn.rds.base.BaseRepo
import io.tn.rds.dao.RoleDao
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface RoleRepo : BaseRepo<RoleDao, String> {
  fun findAllByName(name: String): List<io.tn.rds.dao.RoleDao>

  @Query(
    """
    from RoleDao r
    left join RoleGroupRoleDao rgr on r.id = rgr.roleId
    left join UserRoleGroupDao urg on rgr.roleGroupId = urg.roleGroupId
    where urg.userId = :userId
  """
  )
  fun findAllByUserId(userId: String): List<io.tn.rds.dao.RoleDao>
}
