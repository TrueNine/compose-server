package com.truenine.component.rds.repo

import com.truenine.component.rds.base.BaseRepo
import com.truenine.component.rds.dao.UserInfoDao
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
interface UserInfoRepo : BaseRepo<UserInfoDao, String> {
  fun findByUserId(userId: String): UserInfoDao?

  @Transactional(rollbackFor = [Exception::class])
  fun deleteByPhone(phone: String): Int
}
