package io.tn.rds.repo

import io.tn.rds.base.BaseRepo
import io.tn.rds.dao.UserGroupRoleGroupDao
import org.springframework.stereotype.Repository

@Repository
interface UserGroupRoleGroupRepo : BaseRepo<UserGroupRoleGroupDao, String> {
  fun findAllByUserGroupId(userGroupId: String): List<io.tn.rds.dao.UserGroupRoleGroupDao>
  fun existsByUserGroupIdAndRoleGroupId(
    userGroupId: String,
    roleGroupId: String
  ): Boolean

  fun deleteByUserGroupIdAndRoleGroupId(
    userGroupId: String,
    roleGroupId: String
  )

  fun deleteAllByUserGroupId(userGroupId: String)
}
