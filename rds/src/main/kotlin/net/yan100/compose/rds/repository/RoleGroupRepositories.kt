package net.yan100.compose.rds.repository

import net.yan100.compose.rds.base.BaseRepository
import net.yan100.compose.rds.entity.FullRoleGroup
import net.yan100.compose.rds.entity.RoleGroup
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface RoleGroupRepo : BaseRepository<RoleGroup> {
  fun findAllByName(name: String): List<RoleGroup>

  @Query(
    """
    from RoleGroup rg
    left join UserRoleGroup ur on rg.id = ur.roleGroupId
    where ur.userId = :userId
  """
  )
  fun findAllByUserId(userId: Long): List<RoleGroup>
}

@Repository
interface FullRoleGroupEntityRepo : BaseRepository<FullRoleGroup> {
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
