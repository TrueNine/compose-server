package com.truenine.component.rds.repo

import com.truenine.component.rds.base.BaseRepo
import com.truenine.component.rds.entity.UserGroupEntity
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface UserGroupRepo : BaseRepo<UserGroupEntity, String> {
  @Query(
      """
    from UserGroupEntity ug
    left join UserGroupUserEntity ugu
    on ug.id = ugu.userGroupId
    where ug.userId = :userId
    or ugu.userId = :userId
  """
  )
  fun findAllByUserId(userId: String): Set<UserGroupEntity>

  fun existsByIdAndUserId(id: String, userId: String): Boolean
}
