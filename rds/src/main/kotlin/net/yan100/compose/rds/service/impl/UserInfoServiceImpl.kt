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
package net.yan100.compose.rds.service.impl

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.yan100.compose.core.RefId
import net.yan100.compose.core.hasText
import net.yan100.compose.core.string
import net.yan100.compose.rds.core.ICrud
import net.yan100.compose.rds.core.entities.fromDbData
import net.yan100.compose.rds.core.entities.withNew
import net.yan100.compose.rds.core.jpa
import net.yan100.compose.rds.entities.UserInfo
import net.yan100.compose.rds.entities.Usr
import net.yan100.compose.rds.repositories.IUserInfoRepo
import net.yan100.compose.rds.repositories.IUsrRepo
import net.yan100.compose.rds.service.IUserInfoService
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserInfoServiceImpl(
    private val userRepo: IUsrRepo,
    private val infoRepo: IUserInfoRepo
) : IUserInfoService, ICrud<UserInfo> by jpa(infoRepo, Usr::class) {
  override suspend fun findIsRealPeopleById(id: RefId): Boolean = infoRepo.existsByIdAndIsRealPeople(id)

  override suspend fun findIsRealPeopleByUserId(userId: RefId): Boolean =
    withContext(Dispatchers.IO) { infoRepo.findFirstByUserIdAndPriIsTrue(userId)?.run { infoRepo.existsByIdAndIsRealPeople(id) } ?: false }

  override fun existsByFirstNameAndLastName(firstName: String, lastName: String): Boolean {
    return infoRepo.existsAllByFirstNameAndLastName(firstName, lastName)
  }

  override fun existsByIdCard(idCard: string): Boolean {
    return infoRepo.existsAllByIdCard(idCard)
  }

  override fun groupByUserIdByUserIds(userIds: List<RefId>): Map<RefId, List<UserInfo>> {
    return infoRepo.findAllByUserId(userIds).groupBy { it.userId!! }
  }

  override fun countAllByHasUser(): Long {
    return infoRepo.countAllByHasUser()
  }

  @Transactional(rollbackFor = [Exception::class])
  override fun deleteUserInfoAndUser(userInfoId: RefId) {
    infoRepo.findByIdOrNull(userInfoId)?.also { i ->
      if (i.userId.hasText()) userRepo.deleteById(i.userId!!)
      removeById(i.id)
    }
  }

  @Transactional(rollbackFor = [Exception::class])
  override fun postFound(e: UserInfo): UserInfo {
    // 如果存在身份证，则匹配相同的身份证
    return e.idCard?.let { c ->
      if (infoRepo.existsAllByIdCard(c)) {
        postAll(
          infoRepo.findAllByIdCard(c).mapIndexed { index, r ->
            val d = e.fromDbData(r)
            d.apply { pri = index == 0 }
          }
        ).first()
      } else null
    } ?: e.phone?.let { c ->
      if (infoRepo.existsAllByPhone(c)) {
        val phoneList = infoRepo.findAllByPhone(c).mapIndexed { index, r ->
          val d = e.fromDbData(r)
          d.apply { pri = index == 0 }
        }
        postAll(phoneList).first()
      } else null
    } ?: post(e.withNew())
  }

  override fun savePlainUserInfoByUser(createUserId: RefId, usr: Usr): UserInfo {
    return infoRepo.save(
      UserInfo().apply {
        this.createUserId = createUserId
        this.userId = usr.id
        this.pri = true
      }
    )
  }

  override fun findAllIdByUserId(userId: RefId): List<RefId> {
    return infoRepo.findAllIdByUserId(userId)
  }

  override fun findUserIdById(id: RefId): RefId? {
    return infoRepo.findUserIdById(id)
  }

  override fun findUserByWechatOpenId(openId: String): Usr? {
    return infoRepo.findUserByWechatOpenId(openId)
  }

  override fun findUserByPhone(phone: String): Usr? {
    return infoRepo.findUserByPhone(phone)
  }

  override fun findByUserId(userId: String): UserInfo? {
    return infoRepo.findFirstByUserIdAndPriIsTrue(userId)
  }

  override fun existsByPhone(phone: string): Boolean {
    return infoRepo.existsByPhone(phone)
  }

  override fun existsByWechatOpenId(openId: String): Boolean {
    return infoRepo.existsByWechatOpenid(openId)
  }
}
