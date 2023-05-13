package net.yan100.compose.rds.repository

import net.yan100.compose.rds.base.BaseRepository
import net.yan100.compose.rds.entity.AllRoleEntity
import net.yan100.compose.rds.entity.RoleEntity
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface RoleRepository : BaseRepository<RoleEntity> {
  fun findAllByName(name: String): List<RoleEntity>

  @Query(
    """
    from RoleEntity r
    left join RoleGroupRoleEntity rgr on r.id = rgr.roleId
    left join UserRoleGroupEntity urg on rgr.roleGroupId = urg.roleGroupId
    where urg.userId = :userId
  """
  )
  fun findAllByUserId(userId: Long): List<RoleEntity>
}

@Repository
interface AllRoleEntityRepository : BaseRepository<AllRoleEntity> {
  fun findAllByName(name: String): List<AllRoleEntity>

  @Query(
    """
    from AllRoleEntity r
    left join RoleGroupRoleEntity rgr on r.id = rgr.roleId
    left join UserRoleGroupEntity urg on rgr.roleGroupId = urg.roleGroupId
    where urg.userId = :userId
  """
  )
  fun findAllByUserId(userId: Long): List<AllRoleEntity>
}
