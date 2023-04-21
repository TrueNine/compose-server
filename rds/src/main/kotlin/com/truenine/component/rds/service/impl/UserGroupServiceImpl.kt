package com.truenine.component.rds.service.impl

import com.truenine.component.rds.base.BaseServiceImpl
import com.truenine.component.rds.entity.UserGroupEntity
import com.truenine.component.rds.entity.relationship.UserGroupUserEntity
import com.truenine.component.rds.repository.UserGroupRepository
import com.truenine.component.rds.repository.UserGroupUserRepository
import com.truenine.component.rds.service.UserGroupService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserGroupServiceImpl(
  private val userGroupRepo: UserGroupRepository,
  private val userGroupUserRepo: UserGroupUserRepository
) : UserGroupService, BaseServiceImpl<UserGroupEntity>(userGroupRepo) {

  @Transactional(rollbackFor = [Exception::class])
  override fun assignUserToUserGroup(userId: Long, userGroupId: Long) {
    val isLeader = userGroupUserRepo.existsByUserGroupIdAndUserId(userGroupId, userId)
    val isMember = userGroupRepo.existsByIdAndUserId(userGroupId, userId)
    if (!(isMember || isLeader)) {
      userGroupUserRepo.save(
        UserGroupUserEntity().apply {
          this.userGroupId = userGroupId
          this.userId = userId
        })
    }
  }

  override fun findAllUserGroupByUserId(userId: Long): Set<UserGroupEntity> = userGroupRepo.findAllByUserId(userId).toSet()
}
