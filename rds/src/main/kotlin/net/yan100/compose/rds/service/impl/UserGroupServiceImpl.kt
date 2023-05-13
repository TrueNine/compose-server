package net.yan100.compose.rds.service.impl


import net.yan100.compose.rds.base.BaseServiceImpl
import net.yan100.compose.rds.entity.UserGroupEntity
import net.yan100.compose.rds.repository.UserGroupRepository
import net.yan100.compose.rds.repository.relationship.UserGroupUserRepository
import net.yan100.compose.rds.service.UserGroupService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserGroupServiceImpl(
  private val userGroupRepo: UserGroupRepository,
  private val userGroupUserRepo: UserGroupUserRepository
) : UserGroupService, BaseServiceImpl<UserGroupEntity>(userGroupRepo) {

  @Transactional(rollbackFor = [Exception::class])
  override fun saveUserToUserGroup(userId: String, userGroupId: String) {
    val isLeader = userGroupUserRepo.existsByUserGroupIdAndUserId(userGroupId, userId)
    val isMember = userGroupRepo.existsByIdAndUserId(userGroupId, userId)
    if (!(isMember || isLeader)) {
      userGroupUserRepo.save(
        net.yan100.compose.rds.entity.relationship.UserGroupUserEntity().apply {
          this.userGroupId = userGroupId
          this.userId = userId
        })
    }
  }

  override fun findAllByLeaderUserId(userId: String): Set<UserGroupEntity> = userGroupRepo.findAllByUserId(userId).toSet()

  override fun findAllByUserAccount(account: String): Set<UserGroupEntity> =
    userGroupRepo.findAllByUserAccount(account).toSet()
}
