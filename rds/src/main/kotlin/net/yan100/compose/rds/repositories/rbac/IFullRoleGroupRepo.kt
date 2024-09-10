package net.yan100.compose.rds.repositories.rbac

import net.yan100.compose.rds.entities.FullRoleGroup
import net.yan100.compose.rds.repositories.base.IRepo
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface IFullRoleGroupRepo : IRepo<FullRoleGroup> {
  fun findAllByName(name: String): List<FullRoleGroup>

  @Query(
    """
    from FullRoleGroup rg
    left join UserRoleGroup ur on rg.id = ur.roleGroupId
    where ur.userId = :userId
  """
  )
  fun findAllByUserId(userId: Long): List<FullRoleGroup>
}
