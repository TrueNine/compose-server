package com.truenine.component.rds.service.impl

import com.truenine.component.core.consts.CacheFieldNames
import com.truenine.component.core.lang.Str
import com.truenine.component.rds.entity.*
import com.truenine.component.rds.models.UserAuthorizationModel
import com.truenine.component.rds.models.req.PutUserGroupRequestParam
import com.truenine.component.rds.models.req.PutUserRequestParam
import com.truenine.component.rds.service.RbacService
import com.truenine.component.rds.service.UserAdminService
import com.truenine.component.rds.service.UserGroupService
import com.truenine.component.rds.service.UserService
import jakarta.validation.Valid
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Cacheable
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
open class UserAdminServiceImpl
  : UserAdminService {

  private lateinit var userService: UserService

  @Autowired
  fun setUserService(userService: UserService) {
    this.userService = userService
  }

  private lateinit var rbacService: RbacService

  @Autowired
  fun setRbacSerVice(rbacService: RbacService) {
    this.rbacService = rbacService
  }

  private lateinit var passwordEncoder: PasswordEncoder

  @Autowired
  fun setPasswordEncoder(passwordEncoder: PasswordEncoder) {
    this.passwordEncoder = passwordEncoder
  }

  private lateinit var userGroupService: UserGroupService

  @Autowired
  fun setUserGroupService(userGroupService: UserGroupService) {
    this.userGroupService = userGroupService
  }


  @Transactional(rollbackFor = [Exception::class])
  override fun registerPlainUser(putUserRequestParam: PutUserRequestParam?): UserEntity? {
    return putUserRequestParam?.takeIf {
      Str.hasText(it.account)
        && Str.hasText(it.pwd)
        && Str.hasText(it.againPwd)
        && Str.hasText(it.nickName)
        && !(userService.existsByAccount(it.account!!))
    }?.let {
      val u =
        UserEntity().apply {
          account = it.account
          nickName = it.nickName
          doc = it.doc
          pwdEnc = passwordEncoder.encode(it.pwd)
        }
      val savedUser = userService.saveUser(u)!!
      rbacService.assignRoleGroupToUser(
        rbacService.findPlainRoleGroup(),
        savedUser
      )
      savedUser
    }
  }

  @Transactional(rollbackFor = [Exception::class])
  override fun registerRootUser(rootPutUserRequestParam: PutUserRequestParam): UserEntity? {
    return (registerPlainUser(rootPutUserRequestParam)
      ?: findUserByAccount(rootPutUserRequestParam.account))
      ?.apply {
        rbacService.assignRoleGroupToUser(
          rbacService.findRootRoleGroup(),
          this
        )
      }
  }

  @Transactional(rollbackFor = [Exception::class])
  override fun completionUserInfo(userInfo: UserInfoEntity): UserInfoEntity? =
    userService.saveUserInfo(userInfo)

  @Transactional(rollbackFor = [Exception::class])
  override fun completionUserInfoByAccount(
    account: String?,
    userInfo: UserInfoEntity
  ): UserInfoEntity? = account?.run {
    userService.findUserByAccount(this)?.run {
      userInfo.userId = this.id
      completionUserInfo(userInfo)
    }
  }

  @Transactional(rollbackFor = [Exception::class])
  override fun updatePasswordByAccountAndOldPassword(
    account: String?,
    oldPwd: String?,
    newPwd: String?
  ): UserAuthorizationModel? {
    return takeIf {
      Str.hasText(account)
        && Str.hasText(oldPwd)
        && Str.hasText(newPwd)
    }?.run {
      // 账号不为空
      findUserAuthorizationModelByAccount(account!!)?.apply {
        if (!passwordEncoder.matches(newPwd, user.pwdEnc)) {
          this.user.pwdEnc = passwordEncoder.encode(newPwd)
          userService.saveUser(this.user)
        }
      }
    }
  }

  @Transactional(rollbackFor = [Exception::class])
  override fun verifyPassword(account: String?, pwd: String?): Boolean {
    return account?.takeIf {
      pwd != null
    }?.run {
      passwordEncoder.matches(pwd, userService.findPwdEncByAccount(account))
    } ?: false
  }

  @Suppress("SpringElInspection")
  @Cacheable(
    cacheNames = [CacheFieldNames.User.DETAILS],
    key = "#account",
    unless = "#result == null",
    cacheManager = CacheFieldNames.CacheManagerNames.H2
  )
  override fun findUserAuthorizationModelByAccount(account: String): UserAuthorizationModel? {
    return UserAuthorizationModel()
      .apply {
        user = userService.findUserByAccount(account)
      }.takeIf {
        it.user != null
      }?.let {
        runBlocking {
          val a = async {
            userService.findUserInfoById(it.user!!.id)
          }
          val b = async {
            findAllRoleByUser(it.user!!)
          }
          val c = async {
            findAllPermissionsByUser(it.user!!)
          }
          it.info = a.await()
          it.roles = b.await()
          it.permissions = c.await()
          it
        }
      }
  }

  override fun findUserById(id: String?): UserEntity? {
    return id?.run { userService.findUserById(id) }
  }

  override fun findUserByAccount(account: String): UserEntity? =
    userService.findUserByAccount(account)


  override fun findAllRoleGroupByAccount(account: String): Set<RoleGroupEntity> {
    return userService.findUserByAccount(account)
      ?.let {
        findAllRoleGroupByUser(it)
      } ?: setOf()
  }

  override fun findAllRoleGroupByUser(user: UserEntity): Set<RoleGroupEntity> {
    return rbacService.findAllRoleGroupByUser(user)
  }

  override fun findAllRoleByAccount(account: String): Set<RoleEntity> {
    return userService.findUserByAccount(account)
      ?.let {
        findAllRoleByUser(it)
      } ?: setOf()
  }

  override fun findAllRoleByUser(user: UserEntity): Set<RoleEntity> {
    return rbacService.findAllRoleByUser(user)
  }

  override fun findAllPermissionsByAccount(account: String): Set<PermissionsEntity> {
    return userService.findUserByAccount(account)
      ?.let {
        findAllPermissionsByUser(it)
      } ?: setOf()
  }

  override fun findAllPermissionsByUser(user: UserEntity): Set<PermissionsEntity> {
    return rbacService.findAllPermissionsByUser(user)
  }

  @Suppress("SpringElInspection")
  @CacheEvict(
    condition = "#user.account != null",
    cacheNames = [CacheFieldNames.User.DETAILS],
    key = "#user.account",
    cacheManager = CacheFieldNames.CacheManagerNames.H2
  )
  @Transactional(rollbackFor = [Exception::class])
  override fun revokeRoleGroupByUser(
    user: UserEntity,
    roleGroup: RoleGroupEntity
  ) {
    rbacService.revokeRoleGroupByUser(roleGroup, user)
  }

  @Transactional(rollbackFor = [Exception::class])
  override fun registerUserGroup(@Valid dto: PutUserGroupRequestParam): UserGroupEntity? {
    return userService.findUserByAccount(dto.leaderUserAccount)?.let {
      UserGroupEntity().apply {
        this.userId = it.id
        this.name = dto.name
        this.doc = dto.desc
      }.apply {
        userGroupService.saveUserGroup(this)
      }
    }
  }

  override fun findAllUserGroupByUser(user: UserEntity): Set<UserGroupEntity> {
    return userGroupService.findAllUserGroupByUserId(user.id)
  }

  override fun deleteUserByAccount(account: String?) {
    account?.apply {
      userService.findUserByAccount(account)?.apply {
        userService.deleteUser(this)
        userService.findUserInfoByAccount(account)?.apply {
          userService.deleteUserInfo(this)
        }
        rbacService.revokeAllRoleGroupByUser(this)
      }
    }
  }

  @Transactional(rollbackFor = [Exception::class])
  override fun assignUserToUserGroupById(userId: String, userGroupId: String) {
    userGroupService.assignUserToUserGroup(userId, userGroupId)
  }
}
