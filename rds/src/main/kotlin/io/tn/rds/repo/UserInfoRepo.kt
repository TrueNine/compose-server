package io.tn.rds.repo

import io.tn.rds.base.BaseRepo
import io.tn.rds.dao.UserInfoDao
import org.springframework.stereotype.Repository

@Repository
interface UserInfoRepo : BaseRepo<UserInfoDao, String> {
  fun findByUserId(userId: String): UserInfoDao?
}
