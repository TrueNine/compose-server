package com.truenine.component.rds.repo

import com.truenine.component.rds.base.BaseRepo
import com.truenine.component.rds.entity.UserInfoEntity
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
interface UserInfoRepo : BaseRepo<UserInfoEntity, String> {
  fun findByUserId(userId: String): UserInfoEntity?

  @Transactional(rollbackFor = [Exception::class])
  fun deleteByPhone(phone: String): Int
}
