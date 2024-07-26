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
package net.yan100.compose.rds.service

import net.yan100.compose.core.alias.RefId
import net.yan100.compose.core.alias.SerialCode
import net.yan100.compose.rds.entities.account.Usr
import net.yan100.compose.rds.entities.info.UserInfo
import net.yan100.compose.rds.service.base.IService

interface IUserInfoService : IService<UserInfo> {
  suspend fun findIsRealPeopleById(id: RefId): Boolean

  suspend fun findIsRealPeopleByUserId(userId: RefId): Boolean

  fun existsByFirstNameAndLastName(firstName: String, lastName: String): Boolean

  fun existsByIdCard(idCard: SerialCode): Boolean

  /**
   * ## 根据用户 id 列表，获取用户信息列表
   *
   * @return `user id` to `userInfos`
   */
  fun groupByUserIdByUserIds(userIds: List<RefId>): Map<RefId, List<UserInfo>>

  fun countAllByHasUser(): Long

  /**
   * ## 删除用户信息以及其下的账号
   *
   * @param userInfoId 用户信息 id
   */
  fun deleteUserInfoAndUser(userInfoId: RefId)

  /**
   * ## 根据用户，保存一个默认的主要用户信息
   *
   * @param createUserId 创建用户的 id
   * @param usr 用户
   */
  fun savePlainUserInfoByUser(createUserId: RefId, usr: Usr): UserInfo

  /**
   * ## 根据用户，保存一个默认的主要用户信息
   *
   * createUserId 根据当前用户的 createUserId 来计算
   *
   * @param usr 用户
   * @see [savePlainUserInfoByUser]
   */
  fun savePlainUserInfoByUser(usr: Usr): UserInfo = savePlainUserInfoByUser(usr.createUserId, usr)

  fun findAllIdByUserId(userId: RefId): List<RefId>

  fun findUserIdById(id: RefId): RefId?

  fun findUserByWechatOpenId(openId: String): Usr?

  fun findUserByPhone(phone: String): Usr?

  fun findByUserId(userId: String): UserInfo?

  fun existsByPhone(phone: SerialCode): Boolean

  fun existsByWechatOpenId(openId: String): Boolean
}
