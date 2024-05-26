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
import net.yan100.compose.rds.entities.FullUsr
import net.yan100.compose.rds.entities.Usr
import net.yan100.compose.rds.entities.info.UserInfo
import net.yan100.compose.rds.repositories.base.IRepo
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
interface IUsrRepo : IRepo<Usr> {

  fun findByAccount(account: String): Usr?

  @Query("""
    SELECT u.id
    FROM Usr u
    WHERE u.account = :account
  """) fun findIdByAccount(account: String): String

  @Query("""
    SELECT pwdEnc
    FROM Usr
    WHERE account = :account
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
    SELECT r.name
    FROM Usr u
    LEFT JOIN UserRoleGroup urg ON urg.userId = u.id
    LEFT JOIN RoleGroup rg ON rg.id = urg.roleGroupId
    LEFT JOIN RoleGroupRole rgr ON rgr.roleGroupId = rg.id
    LEFT JOIN Role r ON r.id = rgr.roleId
    WHERE u.account = :account
  """
  )
  fun findAllRoleNameByAccount(account: String): Set<String>

  @Query(
    """
    SELECT p.name
    FROM Usr u
    LEFT JOIN UserRoleGroup urg ON urg.userId = u.id
    LEFT JOIN RoleGroup rg ON rg.id = urg.roleGroupId
    LEFT JOIN RoleGroupRole rgr ON rgr.roleGroupId = rg.id
    LEFT JOIN Role r ON r.id = rgr.roleId
    LEFT JOIN RolePermissions rp ON rp.roleId = r.id
    LEFT JOIN Permissions p ON p.id = rp.permissionsId
    WHERE u.account = :account
  """
  )
  fun findAllPermissionsNameByAccount(account: String): Set<String>

  fun existsAllByAccount(account: String): Boolean

  @Modifying @Query("UPDATE Usr u SET u.banTime = :banTime WHERE u.account = :account") fun saveUserBanTimeByAccount(banTime: LocalDateTime?, account: String)

  @Query("""
    SELECT count(i.id) > 0
    FROM UserInfo i
    LEFT JOIN Usr u ON i.userId = u.id
    WHERE i.wechatOpenid = :openId
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
    FROM Usr u
    LEFT JOIN UserInfo i ON u.id = i.userId
    WHERE i.wechatOpenid = :openid
    """)
  fun findUserByWechatOpenId(openid: String): Usr?

  /** 根据 电话号码查询用户手机号 */
  @Query("""
    FROM Usr u
    LEFT JOIN UserInfo i ON u.id = i.userId
    WHERE i.phone = :phone
  """) fun findUserByPhone(phone: String): Usr?

  fun existsByPhone(phone: String): Boolean

  fun existsByWechatOpenid(wechatOpenId: String): Boolean

  @Transactional(rollbackFor = [Exception::class]) fun deleteByPhone(phone: String): Int
}
