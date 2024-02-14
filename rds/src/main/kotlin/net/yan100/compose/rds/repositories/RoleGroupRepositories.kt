package net.yan100.compose.rds.repositories

import net.yan100.compose.rds.entities.FullRoleGroup
import net.yan100.compose.rds.entities.RoleGroup
import net.yan100.compose.rds.repositories.base.IRepo
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface RoleGroupRepo : IRepo<RoleGroup> {
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
interface FullRoleGroupEntityRepo : IRepo<FullRoleGroup> {
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
