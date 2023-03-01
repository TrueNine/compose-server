package io.tn.rds.service.impl

import io.tn.core.id.Snowflake
import io.tn.core.lang.Str
import io.tn.rds.dao.*
import io.tn.rds.dto.UserGroupRegisterDto
import io.tn.rds.dto.UserRegisterDto
import io.tn.rds.service.RbacService
import io.tn.rds.service.UserAdminService
import io.tn.rds.service.UserGroupService
import io.tn.rds.service.UserService
import io.tn.rds.vo.UsrVo
import jakarta.validation.Valid
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import org.springframework.beans.factory.annotation.Autowired
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

  private lateinit var snowflake: Snowflake

  @Autowired
  fun setSnowflake(snowflake: Snowflake) {
    this.snowflake = snowflake
  }

  private lateinit var userGroupService: UserGroupService

  @Autowired
  fun setUserGroupService(userGroupService: UserGroupService) {
    this.userGroupService = userGroupService
  }


  @Transactional(rollbackFor = [Exception::class])
  override fun registerPlainUser(userRegisterDto: UserRegisterDto?): UserDao? {
    return userRegisterDto?.takeIf {
      Str.hasText(it.account)
        && Str.hasText(it.pwd)
        && Str.hasText(it.againPwd)
        && Str.hasText(it.nickName)
        && !(userService.existsByAccount(it.account!!))
    }?.let {
      val u = UserDao().apply {
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
  override fun registerRootUser(rootUserRegisterDto: UserRegisterDto): UserDao? {
    return (registerPlainUser(rootUserRegisterDto)
      ?: findUserByAccount(rootUserRegisterDto.account))
      ?.apply {
        rbacService.assignRoleGroupToUser(
          rbacService.findRootRoleGroup(),
          this
        )
      }
  }

  @Transactional(rollbackFor = [Exception::class])
  override fun completionUserInfo(userInfo: UserInfoDao): UserInfoDao? =
    userService.saveUserInfo(userInfo)

  @Transactional(rollbackFor = [Exception::class])
  override fun completionUserInfoByAccount(
    account: String?,
    userInfo: UserInfoDao
  ): UserInfoDao? = account?.run {
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
  ): UsrVo? {
    return takeIf {
      Str.hasText(account)
        && Str.hasText(oldPwd)
        && Str.hasText(newPwd)
    }?.run {
      // 账号不为空
      findUsrVoByAccount(account!!)?.apply {
        if (!passwordEncoder.matches(newPwd, user.pwdEnc)) {
          this.user.pwdEnc = passwordEncoder.encode(newPwd)
          userService.saveUser(this.user)
        }
      }
    }
  }

  override fun verifyPassword(account: String?, pwd: String?): Boolean {
    return account?.takeIf {
      pwd != null
    }?.run {
      passwordEncoder.matches(pwd, userService.findPwdEncByAccount(account))
    } ?: false
  }


  override fun findUsrVoByAccount(account: String): UsrVo? {
    return UsrVo().apply {
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
        it.tenant = it.user!!.cti
        it.info = a.await()
        it.roles = b.await()
        it.permissions = c.await()
        it
      }
    }
  }

  override fun findUserById(id: String?): UserDao? {
    return id?.run { userService.findUserById(id) }
  }

  override fun findUserByAccount(account: String): UserDao? =
    userService.findUserByAccount(account)


  override fun findAllRoleGroupByAccount(account: String): Set<RoleGroupDao> {
    return userService.findUserByAccount(account)
      ?.let {
        findAllRoleGroupByUser(it)
      } ?: setOf()
  }

  override fun findAllRoleGroupByUser(user: UserDao): Set<RoleGroupDao> {
    return rbacService.findAllRoleGroupByUser(user)
  }

  override fun findAllRoleByAccount(account: String): Set<RoleDao> {
    return userService.findUserByAccount(account)
      ?.let {
        findAllRoleByUser(it)
      } ?: setOf()
  }

  override fun findAllRoleByUser(user: UserDao): Set<RoleDao> {
    return rbacService.findAllRoleByUser(user)
  }

  override fun findAllPermissionsByAccount(account: String): Set<PermissionsDao> {
    return userService.findUserByAccount(account)
      ?.let {
        findAllPermissionsByUser(it)
      } ?: setOf()
  }

  override fun findAllPermissionsByUser(user: UserDao): Set<PermissionsDao> {
    return rbacService.findAllPermissionsByUser(user)
  }

  @Transactional(rollbackFor = [Exception::class])
  override fun revokeRoleGroupByUser(
    user: UserDao,
    roleGroup: RoleGroupDao
  ) {
    rbacService.revokeRoleGroupByUser(roleGroup, user)
  }

  @Transactional(rollbackFor = [Exception::class])
  override fun registerUserGroup(@Valid dto: UserGroupRegisterDto): UserGroupDao? {
    return userService.findUserByAccount(dto.leaderUserAccount)?.let {
      UserGroupDao().apply {
        this.userId = it.id
        this.name = dto.name
        this.doc = dto.desc
      }.apply {
        userGroupService.saveUserGroup(this)
      }
    }
  }

  override fun findAllUserGroupByUser(user: UserDao): Set<UserGroupDao> {
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
