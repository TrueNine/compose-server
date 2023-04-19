package com.truenine.component.rds.service

import com.truenine.component.rds.base.BaseService
import com.truenine.component.rds.entity.UserEntity
import java.time.LocalDateTime

interface UserService : BaseService<UserEntity> {
  fun findUserByAccount(account: String): UserEntity?
  fun findPwdEncByAccount(account: String): String?
  fun existsByAccount(account: String): Boolean
  fun modifyUserBandTimeTo(account: String, dateTime: LocalDateTime?)
}
