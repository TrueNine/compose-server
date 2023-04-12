package com.truenine.component.rds.service.impl

import com.truenine.component.rds.entity.UserGroupEntity
import com.truenine.component.rds.entity.UserGroupUserEntity
import com.truenine.component.rds.repo.UserGroupRepo
import com.truenine.component.rds.repo.UserGroupUserRepo
import com.truenine.component.rds.service.UserGroupService
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
open class UserGroupServiceImpl(
  private val userGroupRepo: UserGroupRepo,
  private val userGroupUserRepo: UserGroupUserRepo
) : UserGroupService {


  override fun saveUserGroup(userGroup: UserGroupEntity): UserGroupEntity? {
    return userGroupRepo.save(userGroup)
  }

  @Transactional(rollbackFor = [Exception::class])
  override fun assignUserToUserGroup(userId: String, userGroupId: String) {
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


  override fun deleteUserGroupById(id: String) {
    userGroupRepo.deleteById(id)
  }

  override fun findUserGroupById(id: String): UserGroupEntity? {
    return userGroupRepo.findByIdOrNull(id)
  }

  override fun findAllUserGroupByUserId(userId: String): Set<UserGroupEntity> {
    return userGroupRepo.findAllByUserId(userId)
  }
}
