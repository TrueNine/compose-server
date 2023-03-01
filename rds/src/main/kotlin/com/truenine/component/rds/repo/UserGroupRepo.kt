package com.truenine.component.rds.repo

import com.truenine.component.rds.base.BaseRepo
import com.truenine.component.rds.dao.UserGroupDao
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface UserGroupRepo : BaseRepo<UserGroupDao, String> {
  @Query(
    """
    from UserGroupDao ug
    left join UserGroupUserDao ugu
    on ug.id = ugu.userGroupId
    where ug.userId = :userId
    or ugu.userId = :userId
  """
  )
  fun findAllByUserId(userId: String): Set<com.truenine.component.rds.dao.UserGroupDao>

  fun existsByIdAndUserId(id: String, userId: String): Boolean
}
