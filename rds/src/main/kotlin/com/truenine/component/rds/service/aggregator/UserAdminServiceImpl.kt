package com.truenine.component.rds.service.aggregator

import com.truenine.component.core.consts.CacheFieldNames
import com.truenine.component.core.lang.hasText
import com.truenine.component.core.lang.requireAll
import com.truenine.component.rds.entity.*
import com.truenine.component.rds.models.UserAuthorizationModel
import com.truenine.component.rds.models.req.PostUserGroupRequestParam
import com.truenine.component.rds.models.req.PostUserRequestParam
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
class UserAdminServiceImpl : UserAdminService {

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
  override fun registerPlainUser(userReq: PostUserRequestParam): UserEntity? {
    requireAll(
      hasText(userReq.account),
      hasText(userReq.pwd),
      hasText(userReq.againPwd),
      hasText(userReq.nickName)
    ) {
      "参数不合法 $userReq"
    }
    require(userService.notExistsByAccount(userReq.account))
    { "账户已经存在" }

    val newUser = UserEntity().apply {
      account = userReq.account
      nickName = userReq.nickName
      doc = userReq.doc
      pwdEnc = passwordEncoder.encode(userReq.pwd)
    }
    val plainRoleGroup = rbacService.findPlainRoleGroup()!!
    val savedUser = userService.saveUser(newUser)
    checkNotNull(savedUser) { "没有注册新用户" }
    rbacService.assignRoleGroupToUser(plainRoleGroup, savedUser)
    return savedUser
  }

  @Transactional(rollbackFor = [Exception::class])
  override fun registerRootUser(rootPostUserRequestParam: PostUserRequestParam): UserEntity? {
    val plain = registerPlainUser(rootPostUserRequestParam)
    checkNotNull(plain)
    val rootRoleGroup = rbacService.findRootRoleGroup()!!
    rbacService.assignRoleGroupToUser(rootRoleGroup, plain)
    return plain
  }

  override fun completionUserInfo(userInfo: UserInfoEntity): UserInfoEntity? = userService.saveUserInfo(userInfo)

  override fun completionUserInfoByAccount(
    account: String,
    userInfo: UserInfoEntity
  ): UserInfoEntity? = account.run {
    userService.findUserByAccount(this)?.run {
      userInfo.userId = this.id
      completionUserInfo(userInfo)
    }
  }

  @Suppress("SpringElInspection")
  @CacheEvict(
    cacheNames = [CacheFieldNames.User.DETAILS],
    key = "#account",
    cacheManager = CacheFieldNames.CacheManagerNames.H2
  )
  @Transactional(rollbackFor = [Exception::class])
  override fun updatePasswordByAccountAndOldPassword(
    account: String,
    oldPwd: String,
    newPwd: String
  ): UserEntity? {
    // 账号不为空
    requireAll(
      hasText(account),
      hasText(oldPwd),
      hasText(newPwd)
    )
    val user = userService.findUserByAccount(account)
    checkNotNull(user)
    if (passwordEncoder.matches(oldPwd, user.pwdEnc)) {
      return userService.saveUser(user.apply { pwdEnc = passwordEncoder.encode(newPwd) })
    }
    return null
  }

  override fun verifyPassword(account: String?, pwd: String?): Boolean {
    requireAll(
      hasText(account),
      hasText(pwd)
    )
    val pwdEnc = userService.findPwdEncByAccount(account!!)
    return passwordEncoder.matches(pwd, pwdEnc)
  }

  @Suppress("SpringElInspection")
  @Cacheable(
    cacheNames = [CacheFieldNames.User.DETAILS],
    key = "#account",
    unless = "#result == null",
    cacheManager = CacheFieldNames.CacheManagerNames.H2
  )
  override fun findUserAuthorizationModelByAccount(account: String): UserAuthorizationModel? {
    val u = userService.findUserByAccount(account)
    checkNotNull(u)
    val authInfo = UserAuthorizationModel()
    authInfo.user = u
    return runBlocking {
      val info = async {
        userService.findUserInfoById(u.id)
      }
      val roles = async {
        findAllRoleByUser(u)
      }
      val permissions = async {
        findAllPermissionsByUser(u)
      }
      authInfo.info = info.await()
      authInfo.roles = roles.await()
      authInfo.permissions = permissions.await()
      authInfo
    }
  }

  override fun findUserById(id: Long): UserEntity? {
    return userService.findUserById(id)
  }

  override fun findUserByAccount(account: String): UserEntity? =
    userService.findUserByAccount(account)


  override fun findAllRoleGroupByAccount(account: String): Set<RoleGroupEntity> {
    return userService.findUserByAccount(account)
      ?.run { findAllRoleGroupByUser(this) } ?: setOf()
  }

  override fun findAllRoleGroupByUser(user: UserEntity): Set<RoleGroupEntity> = rbacService.findAllRoleGroupByUser(user)


  override fun findAllRoleByAccount(account: String): Set<RoleEntity> = userService.findUserByAccount(account)?.run { findAllRoleByUser(this) } ?: setOf()


  override fun findAllRoleByUser(user: UserEntity): Set<RoleEntity> = rbacService.findAllRoleByUser(user)


  override fun findAllPermissionsByAccount(account: String): Set<PermissionsEntity> =
    userService.findUserByAccount(account)?.run { findAllPermissionsByUser(this) } ?: setOf()


  override fun findAllPermissionsByUser(user: UserEntity): Set<PermissionsEntity> = rbacService.findAllPermissionsByUser(user)


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

  override fun registerUserGroup(@Valid req: PostUserGroupRequestParam): UserGroupEntity? {
    return userService.findUserByAccount(req.leaderUserAccount)?.let { leaderUser ->
      UserGroupEntity().apply {
        userId = leaderUser.id
        name = req.name
        doc = req.desc
      }.apply {
        userGroupService.save(this)
      }
    }
  }

  override fun findAllUserGroupByUser(user: UserEntity): Set<UserGroupEntity> = userGroupService.findAllUserGroupByUserId(user.id)

  @Transactional(rollbackFor = [Exception::class])
  override fun deleteUserByAccount(account: String) {
    userService.findUserByAccount(account)?.apply {
      userService.deleteUser(this)
      userService.findUserInfoByAccount(account)?.apply {
        userService.deleteUserInfo(this)
      }
      rbacService.revokeAllRoleGroupByUser(this)
    }
  }

  override fun assignUserToUserGroupById(userId: Long, userGroupId: Long) = userGroupService.assignUserToUserGroup(userId, userGroupId)

}
