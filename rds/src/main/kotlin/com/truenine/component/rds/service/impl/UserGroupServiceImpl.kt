package com.truenine.component.rds.service.impl

import com.truenine.component.rds.dao.UserGroupDao
import com.truenine.component.rds.dao.UserGroupUserDao
import com.truenine.component.rds.repo.UserGroupRepo
import com.truenine.component.rds.repo.UserGroupUserRepo
import com.truenine.component.rds.service.UserGroupService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
open class UserGroupServiceImpl(
  private val userGroupRepo: UserGroupRepo,
  private val userGroupUserRepo: UserGroupUserRepo
) : UserGroupService {

  @Transactional(rollbackFor = [Exception::class])
  override fun saveUserGroup(userGroup: UserGroupDao): UserGroupDao? {
    return userGroupRepo.save(userGroup)
  }

  @Transactional(rollbackFor = [Exception::class])
  override fun assignUserToUserGroup(userId: String, userGroupId: String) {
    if (!userGroupUserRepo.existsByUserIdAndUserGroupId(userId, userGroupId)
      && !userGroupRepo.existsByIdAndUserId(userGroupId, userId)
    ) {
      userGroupUserRepo.save(
        UserGroupUserDao()
          .apply {
            this.userGroupId = userGroupId
            this.userId = userId
          })
    }
  }

  @Transactional(rollbackFor = [Exception::class])
  override fun deleteUserGroupById(id: String) {
    userGroupRepo.deleteById(id)
  }

  override fun findUserGroupById(id: String): UserGroupDao? {
    return userGroupRepo.findById(id).orElse(null)
  }

  override fun findAllUserGroupByUserId(userId: String): Set<UserGroupDao> {
    return userGroupRepo.findAllByUserId(userId)
  }

}
