package com.truenine.component.rds.service.impl

import com.truenine.component.rds.base.BaseServiceImpl
import com.truenine.component.rds.entity.UserGroupEntity
import com.truenine.component.rds.entity.UserGroupUserEntity
import com.truenine.component.rds.repo.UserGroupRepo
import com.truenine.component.rds.repo.UserGroupUserRepo
import com.truenine.component.rds.service.UserGroupService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserGroupServiceImpl(
  private val userGroupRepo: UserGroupRepo,
  private val userGroupUserRepo: UserGroupUserRepo
) : UserGroupService, BaseServiceImpl<UserGroupEntity>(userGroupRepo) {

  @Transactional(rollbackFor = [Exception::class])
  override fun assignUserToUserGroup(userId: Long, userGroupId: Long) {
    if (!userGroupUserRepo.existsByUserIdAndUserGroupId(userId, userGroupId)
      && !userGroupRepo.existsByIdAndUserId(userGroupId, userId)
    ) {
      userGroupUserRepo.save(
        UserGroupUserEntity()
          .apply {
            this.userGroupId = userGroupId
            this.userId = userId
          })
    }
  }

  override fun findAllUserGroupByUserId(userId: Long): Set<UserGroupEntity> {
    return userGroupRepo.findAllByUserId(userId)
  }
}
