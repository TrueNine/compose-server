package com.truenine.component.rds.repo

import com.truenine.component.rds.base.BaseRepo
import com.truenine.component.rds.dao.UserInfoDao
import org.springframework.stereotype.Repository

@Repository
interface UserInfoRepo : BaseRepo<UserInfoDao, String> {
  fun findByUserId(userId: String): UserInfoDao?
}
