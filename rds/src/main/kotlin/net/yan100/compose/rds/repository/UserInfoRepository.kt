package net.yan100.compose.rds.repository

import net.yan100.compose.rds.base.BaseRepository
import net.yan100.compose.rds.entity.UserInfoEntity
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
interface UserInfoRepository : BaseRepository<UserInfoEntity> {
  fun findByUserId(userId: String): UserInfoEntity?

  @Transactional(rollbackFor = [Exception::class])
  fun deleteByPhone(phone: String): Int
}
