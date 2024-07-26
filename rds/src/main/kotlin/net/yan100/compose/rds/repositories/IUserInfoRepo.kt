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

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import net.yan100.compose.core.alias.RefId
import net.yan100.compose.core.alias.TODO
import net.yan100.compose.rds.entities.account.Usr
import net.yan100.compose.rds.entities.info.UserInfo
import net.yan100.compose.rds.repositories.base.IRepo
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
interface IUserInfoRepo : IRepo<UserInfo> {
  fun existsAllByFirstNameAndLastName(firstName: String, lastName: String): Boolean

  fun existsAllByIdAndIdCardIsNotNull(id: RefId): Boolean

  fun existsAllByIdAndPhoneIsNotNull(id: RefId): Boolean

  fun existsAllByIdAndFirstNameIsNotNull(id: RefId): Boolean

  fun existsAllByIdAndLastNameIsNotNull(id: RefId): Boolean

  suspend fun existsByIdAndIsRealPeople(id: RefId): Boolean =
    withContext(Dispatchers.IO) {
      if (!existsById(id)) false
      else
        listOf(
            async { existsAllByIdAndIdCardIsNotNull(id) },
            async { existsAllByIdAndPhoneIsNotNull(id) },
            async { existsAllByIdAndFirstNameIsNotNull(id) },
            async { existsAllByIdAndLastNameIsNotNull(id) },
          )
          .awaitAll()
          .all { it }
    }

  @Query(
    """
    select count(i.id)
    from UserInfo i
    join Usr u on u.id = i.userId
  """
  )
  fun countAllByHasUser(): Long

  fun existsAllByPhone(phone: String): Boolean

  fun findAllByPhone(phone: String): List<UserInfo>

  fun existsAllByIdCard(idCard: String): Boolean

  fun findAllByIdCard(idCard: String): List<UserInfo>

  @Query(
    """
    select i.id
    from UserInfo i
    where i.userId = :userId
  """
  )
  fun findAllIdByUserId(userId: RefId): List<RefId>

  @Query(
    """
    select i.userId
    from UserInfo i
    where i.id = :id
  """
  )
  fun findUserIdById(id: RefId): RefId?

  @TODO("可会查询出多个用户") fun findByUserId(userId: RefId): UserInfo?

  @Query(
    """
  from UserInfo i
  where i.userId in :userIds
"""
  )
  fun findAllByUserId(userIds: List<RefId>): List<UserInfo>

  fun findFirstByUserIdAndPriIsTrue(userId: RefId): UserInfo?

  /** 根据 微信 openId 查询对应 User */
  @Query(
    """
    from Usr u
    left join UserInfo i ON u.id = i.userId
    where i.wechatOpenid = :openid
    """
  )
  fun findUserByWechatOpenId(openid: String): Usr?

  /** 根据 电话号码查询用户手机号 */
  @Query(
    """
    from Usr u
    left join UserInfo i on u.id = i.userId
    where i.phone = :phone
  """
  )
  fun findUserByPhone(phone: String): Usr?

  fun existsByPhone(phone: String): Boolean

  fun existsByWechatOpenid(wechatOpenId: String): Boolean

  @Transactional(rollbackFor = [Exception::class]) fun deleteByPhone(phone: String): Int
}
