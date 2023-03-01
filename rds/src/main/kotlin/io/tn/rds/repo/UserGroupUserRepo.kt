package io.tn.rds.repo

import io.tn.rds.base.BaseRepo
import io.tn.rds.dao.UserGroupUserDao
import org.springframework.stereotype.Repository

@Repository
interface UserGroupUserRepo : BaseRepo<UserGroupUserDao, String> {
  fun existsByUserIdAndUserGroupId(userId: String, userGroupId: String): Boolean
}
