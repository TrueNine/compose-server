package com.truenine.component.rds.repository

import com.truenine.component.rds.base.BaseRepository
import com.truenine.component.rds.entity.AllRoleGroupEntity
import com.truenine.component.rds.entity.RoleGroupEntity
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface RoleGroupRepository : BaseRepository<RoleGroupEntity> {
  fun findAllByName(name: String): List<RoleGroupEntity>

  @Query(
    """
    from RoleGroupEntity rg
    left join UserRoleGroupEntity ur on rg.id = ur.roleGroupId
    where ur.userId = :userId
  """
  )
  fun findAllByUserId(userId: Long): List<RoleGroupEntity>
}

@Repository
interface AllRoleGroupEntityRepository : BaseRepository<AllRoleGroupEntity> {
  fun findAllByName(name: String): List<AllRoleGroupEntity>

  @Query(
    """
    from AllRoleGroupEntity rg
    left join UserRoleGroupEntity ur on rg.id = ur.roleGroupId
    where ur.userId = :userId
  """
  )
  fun findAllByUserId(userId: Long): List<AllRoleGroupEntity>
}
