package io.tn.rds.repo

import io.tn.rds.base.BaseRepo
import io.tn.rds.dao.PermissionsDao
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface PermissionsRepo : BaseRepo<PermissionsDao, String> {
  fun findAllByName(name: String): List<io.tn.rds.dao.PermissionsDao>

  @Query(
    """
    from PermissionsDao p
    left join RolePermissionsDao rp on p.id = rp.permissionsId
    left join RoleGroupRoleDao rgr on rp.roleId = rgr.roleId
    left join UserRoleGroupDao urg on rgr.roleGroupId = urg.roleGroupId
    where urg.userId = :userId
  """
  )
  fun findAllByUserId(userId: String): List<io.tn.rds.dao.PermissionsDao>
}
