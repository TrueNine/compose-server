package com.truenine.component.rds.repository

import com.truenine.component.rds.base.BaseRepository
import com.truenine.component.rds.entity.UserGroupEntity
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface UserGroupRepository : BaseRepository<UserGroupEntity> {
  @Query(
      """
    from UserGroupEntity ug
    left join UserGroupUserEntity ugu
    on ug.id = ugu.userGroupId
    where ug.userId = :userId
    or ugu.userId = :userId
  """
  )
  fun findAllByUserId(userId: Long): MutableList<UserGroupEntity>

  fun existsByIdAndUserId(id: Long, userId: Long): Boolean
}