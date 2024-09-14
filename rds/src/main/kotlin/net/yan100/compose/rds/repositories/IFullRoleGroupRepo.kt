package net.yan100.compose.rds.repositories

import net.yan100.compose.rds.core.IRepo
import net.yan100.compose.rds.entities.FullRoleGroup
import org.springframework.context.annotation.Primary
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Primary
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
