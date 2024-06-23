/*
 *  Copyright (c) 2020-2024 TrueNine. All rights reserved.
 *
 * The following source code is owned, developed and copyrighted by TrueNine
 * (truenine304520@gmail.com) and represents a substantial investment of time, effort,
 * and resources. This software and its components are not to be used, reproduced,
 * distributed, or sublicensed in any form without the express written consent of
 * the copyright owner, except as permitted by law.
 * Any unauthorized use, distribution, or modification of this source code,
 * or any portion thereof, may result in severe civil and criminal penalties,
 * and will be prosecuted to the maximum extent possible under the law.
 * For inquiries regarding usage or redistribution, please contact:
 *     TrueNine
 *     email: <truenine304520@gmail.com>
 *     website: <github.com/TrueNine>
 */
package net.yan100.compose.rds.repositories

import java.time.LocalDateTime
import net.yan100.compose.core.alias.RefId
import net.yan100.compose.rds.entities.account.FullUsr
import net.yan100.compose.rds.entities.account.Usr
import net.yan100.compose.rds.entities.info.UserInfo
import net.yan100.compose.rds.repositories.base.IRepo
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
interface IUsrRepo : IRepo<Usr> {
  @Query("""
    select count(u.id) > 0
    from Usr u
    left join UserInfo i on i.userId = u.id
    where i.id = :userInfoId
  """)
  fun existsByUserInfoId(userInfoId: RefId): Boolean

  fun findByAccount(account: String): Usr?

  @Query("""
    select u.id
    from Usr u
    where u.account = :account
  """) fun findIdByAccount(account: String): String

  @Query("""
    select pwdEnc
    from Usr
    where account = :account
  """) fun findPwdEncByAccount(account: String): String?

  @Query("""
    from Usr u
    left join UserInfo i on i.userId = u.id
    where i.pri = true and i.phone = :phone
  """)
  fun findAccountByUserInfoPhone(phone: String): String?

  @Query("""
    select u.account
    from Usr u
    left join UserInfo i on i.userId = u.id
    where i.pri = true
    and i.wechatOpenid = :openid
  """)
  fun findAccountByUserInfoWechatOpenid(openid: String): String?

  fun findAllByNickName(nickName: String): List<Usr>

  @Query(
    """
    select r.name
    from Usr u
    left join UserRoleGroup urg on urg.userId = u.id
    left join RoleGroup rg on rg.id = urg.roleGroupId
    left join RoleGroupRole rgr on rgr.roleGroupId = rg.id
    left join Role r on r.id = rgr.roleId
    where u.account = :account
  """
  )
  fun findAllRoleNameByAccount(account: String): Set<String>

  @Query(
    """
    select p.name
    from Usr u
    left join UserRoleGroup urg on urg.userId = u.id
    left join RoleGroup rg on rg.id = urg.roleGroupId
    left join RoleGroupRole rgr on rgr.roleGroupId = rg.id
    left join Role r on r.id = rgr.roleId
    left join RolePermissions rp on rp.roleId = r.id
    left join Permissions p on p.id = rp.permissionsId
    where u.account = :account
  """
  )
  fun findAllPermissionsNameByAccount(account: String): Set<String>

  fun existsAllByAccount(account: String): Boolean

  @Modifying @Query("UPDATE Usr u SET u.banTime = :banTime WHERE u.account = :account") fun saveUserBanTimeByAccount(banTime: LocalDateTime?, account: String)

  @Query("""
    select count(i.id) > 0
    from UserInfo i
    left join Usr u on i.userId = u.id
    where i.wechatOpenid = :openId
  """)
  fun existsByWechatOpenId(openId: String): Boolean
}

@Repository
interface IFullUserRepo : IRepo<FullUsr> {
  fun findByAccount(account: String): FullUsr?
}

@Repository
interface IUserInfoRepo : IRepo<UserInfo> {
  @Query("""
    select count(i.id)
    from UserInfo i
    join Usr u on u.id = i.userId
  """) fun countAllByHasUser(): Long

  fun existsAllByPhone(phone: String): Boolean

  fun findAllByPhone(phone: String): List<UserInfo>

  fun existsAllByIdCard(idCard: String): Boolean

  fun findAllByIdCard(idCard: String): List<UserInfo>

  @Query("""
    select i.id
    from UserInfo i
    where i.userId = :userId
  """) fun findAllIdByUserId(userId: RefId): List<RefId>

  @Query("""
    select i.userId
    from UserInfo i
    where i.id = :id
  """) fun findUserIdById(id: RefId): RefId?

  fun findByUserId(userId: String): UserInfo?

  /** 根据 微信 openId 查询对应 User */
  @Query("""
    from Usr u
    left join UserInfo i ON u.id = i.userId
    where i.wechatOpenid = :openid
    """)
  fun findUserByWechatOpenId(openid: String): Usr?

  /** 根据 电话号码查询用户手机号 */
  @Query("""
    from Usr u
    left join UserInfo i on u.id = i.userId
    where i.phone = :phone
  """) fun findUserByPhone(phone: String): Usr?

  fun existsByPhone(phone: String): Boolean

  fun existsByWechatOpenid(wechatOpenId: String): Boolean

  @Transactional(rollbackFor = [Exception::class]) fun deleteByPhone(phone: String): Int
}
