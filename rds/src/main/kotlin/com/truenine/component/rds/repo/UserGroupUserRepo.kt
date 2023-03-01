package com.truenine.component.rds.repo

import com.truenine.component.rds.base.BaseRepo
import com.truenine.component.rds.dao.UserGroupUserDao
import org.springframework.stereotype.Repository

@Repository
interface UserGroupUserRepo : BaseRepo<UserGroupUserDao, String> {
  fun existsByUserIdAndUserGroupId(userId: String, userGroupId: String): Boolean
}
