package io.tn.rds.repo

import io.tn.rds.base.BaseRepo
import io.tn.rds.dao.RoleGroupDao
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface RoleGroupRepo : BaseRepo<RoleGroupDao, String> {
  fun findAllByName(name: String): List<io.tn.rds.dao.RoleGroupDao>

  @Query(
    """
    from RoleGroupDao rg
    left join UserRoleGroupDao ur on rg.id = ur.roleGroupId
    where ur.userId = :userId
  """
  )
  fun findAllByUserId(userId: String): List<io.tn.rds.dao.RoleGroupDao>
}
