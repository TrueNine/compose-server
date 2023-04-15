package com.truenine.component.rds.repository

import com.truenine.component.rds.base.BaseRepository
import com.truenine.component.rds.entity.UserInfoEntity
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
interface UserInfoRepository : BaseRepository<UserInfoEntity> {
  fun findByUserId(userId: Long): UserInfoEntity?

  @Transactional(rollbackFor = [Exception::class])
  fun deleteByPhone(phone: String): Int
}
