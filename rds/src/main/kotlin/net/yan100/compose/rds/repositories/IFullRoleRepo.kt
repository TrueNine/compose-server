package net.yan100.compose.rds.repositories

import net.yan100.compose.rds.core.IRepo
import net.yan100.compose.rds.entities.FullRole
import org.springframework.context.annotation.Primary
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository


@Primary
@Repository
interface IFullRoleRepo : IRepo<FullRole> {
  fun findAllByName(name: String): List<FullRole>

  @Query(
    """
    from FullRole r
    left join RoleGroupRole rgr on r.id = rgr.roleId
    left join UserRoleGroup urg on rgr.roleGroupId = urg.roleGroupId
    where urg.userId = :userId
  """
  )
  fun findAllByUserId(userId: Long): List<FullRole>
}
