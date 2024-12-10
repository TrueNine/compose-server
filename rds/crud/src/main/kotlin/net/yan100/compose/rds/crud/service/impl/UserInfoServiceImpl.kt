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
package net.yan100.compose.rds.crud.service.impl

import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.yan100.compose.core.*
import net.yan100.compose.core.domain.IChinaName
import net.yan100.compose.rds.core.*
import net.yan100.compose.rds.core.annotations.ACID
import net.yan100.compose.rds.core.entities.fromDbData
import net.yan100.compose.rds.core.entities.withNew
import net.yan100.compose.rds.crud.entities.jpa.QUserInfo
import net.yan100.compose.rds.crud.entities.jpa.UserAccount
import net.yan100.compose.rds.crud.entities.jpa.UserInfo
import net.yan100.compose.rds.crud.repositories.jpa.IUserAccountRepo
import net.yan100.compose.rds.crud.repositories.jpa.IUserInfoRepo
import net.yan100.compose.rds.crud.service.IUserInfoService
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Period

@Service
class UserInfoServiceImpl(
  private val userRepo: IUserAccountRepo,
  private val infoRepo: IUserInfoRepo,
  @PersistenceContext
  private val em: EntityManager
) : IUserInfoService, ICrud<UserInfo> by jpa(infoRepo, UserAccount::class) {
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
      if (i.userId.isId()) userRepo.deleteById(i.userId!!)
      removeById(i.id)
    }
  }

  @ACID
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

  override fun savePlainUserInfoByUser(createUserId: RefId, usr: UserAccount): UserInfo {
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

  override fun findUserByWechatOpenId(openId: String): UserAccount? {
    return infoRepo.findUserByWechatOpenId(openId)
  }

  override fun findUserByPhone(phone: String): UserAccount? {
    return infoRepo.findUserByPhone(phone)
  }

  override fun findByUserId(userId: RefId): UserInfo? {
    return infoRepo.findFirstByUserIdAndPriIsTrue(userId)
  }

  override fun existsByPhone(phone: string): Boolean {
    return infoRepo.existsByPhone(phone)
  }

  override fun existsByWechatOpenId(openId: String): Boolean {
    return infoRepo.existsByWechatOpenid(openId)
  }

  override fun fetchAllBy(dto: IUserInfoService.UserInfoFetchParam) = querydsl(QUserInfo.userInfo, em) {
    dto.takeViewModel {
      dto.id?.also { it.isId() takeFinally { Pr.one(fetchById(it)) } }
      dto.userId?.also { it.isId() takeFinally { infoRepo.findAllByUserId(it, Pq[dto]) } }

      dto.idCard?.also { it.hasText() execute { bb.and(q.idCard.eq(it)) } }
      dto.phone?.also { it.hasText() execute { bb.and(q.phone.eq(it)) } }
      dto.birthday?.also { bb.and(q.birthday.eq(it)) }

      dto.email?.also { it.hasText() execute { bb.and(q.email.eq(it)) } }
      dto.addressCode?.also { it.hasText() execute { bb.and(q.addressCode.like("$it%")) } }
      dto.gender?.also { bb.and(q.gender.eq(it)) }

      // 全名 和 姓 名 二选一
      dto.fullName?.also {
        if (it.nonText()) return@also
        val name = IChinaName[it]
        bb.and(q.firstName.eq(name.firstName))
        bb.and(q.lastName.eq(name.lastName))
      } ?: apply {
        dto.firstName?.also { bb.and(q.firstName.like("$it%")) }
        dto.lastName?.also { bb.and(q.lastName.like("$it%")) }
      }
      // =

      dto.remarkName?.also { it.hasText() execute { bb.and(q.remarkName.like("$it%")) } }
      dto.remark?.also { it.hasText() execute { bb.and(q.remark.like("%$it%")) } }

      dto.age?.also {
        (it in 0..120) execute {
          val beYear = date.now() - Period.ofYears(it)
          bb.and(q.birthday.between(beYear, date.now()))
        }
      }
      dto.hasAvatar execute { bb.and(q.avatarImgId.isNotNull) }
      returns { infoRepo.findAll(bb, Pq[dto].toPageable()).toPr() }
    } ?: Pr.empty()
  }
}
