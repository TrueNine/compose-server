package net.yan100.compose.rds.crud.repositories.jpa

import net.yan100.compose.core.RefId
import net.yan100.compose.rds.core.IRepo
import net.yan100.compose.rds.crud.entities.jpa.RoleGroupRole
import org.springframework.context.annotation.Primary
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Primary
@Deprecated("弃用 JPA")
@Repository("IRoleGroupRoleRepository")
interface IRoleGroupRoleRepo : IRepo<RoleGroupRole> {
  fun findByRoleGroupIdAndRoleId(
    roleGroupId: RefId,
    roleId: RefId,
  ): RoleGroupRole?

  @Query("select rr.roleId from RoleGroupRole rr")
  fun findAllRoleIdByRoleGroupId(roleGroupId: RefId): Set<RefId>

  fun findAllByRoleGroupId(roleGroupId: RefId): List<RoleGroupRole>

  fun existsByRoleGroupIdAndRoleId(roleGroupId: RefId, roleId: RefId): Boolean

  fun deleteByRoleGroupIdAndRoleId(roleGroupId: RefId, roleId: RefId)

  fun deleteAllByRoleIdInAndRoleGroupId(
    roleIds: List<RefId>,
    roleGroupId: RefId,
  )
}
