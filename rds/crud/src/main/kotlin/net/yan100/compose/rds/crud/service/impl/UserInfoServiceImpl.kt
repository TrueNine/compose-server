package net.yan100.compose.rds.crud.service.impl

import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.yan100.compose.core.RefId
import net.yan100.compose.core.isId
import net.yan100.compose.core.string
import net.yan100.compose.rds.core.ICrud
import net.yan100.compose.rds.core.annotations.ACID
import net.yan100.compose.rds.core.entities.fromDbData
import net.yan100.compose.rds.core.entities.withNew
import net.yan100.compose.rds.core.jpa
import net.yan100.compose.rds.crud.entities.jpa.UserAccount
import net.yan100.compose.rds.crud.entities.jpa.UserInfo
import net.yan100.compose.rds.crud.repositories.jpa.IUserAccountRepo
import net.yan100.compose.rds.crud.repositories.jpa.IUserInfoRepo
import net.yan100.compose.rds.crud.service.IUserInfoService
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserInfoServiceImpl(
  private val userRepo: IUserAccountRepo,
  private val infoRepo: IUserInfoRepo,
  @PersistenceContext private val em: EntityManager,
) : IUserInfoService, ICrud<UserInfo> by jpa(infoRepo, UserAccount::class) {
  override suspend fun findIsRealPeopleById(id: RefId): Boolean =
    infoRepo.existsByIdAndIsRealPeople(id)

  override suspend fun findIsRealPeopleByUserId(userId: RefId): Boolean =
    withContext(Dispatchers.IO) {
      infoRepo.findFirstByUserIdAndPriIsTrue(userId)?.run {
        infoRepo.existsByIdAndIsRealPeople(id)
      } == true
    }

  override fun existsByFirstNameAndLastName(
    firstName: String,
    lastName: String,
  ): Boolean {
    return infoRepo.existsAllByFirstNameAndLastName(firstName, lastName)
  }

  override fun existsByIdCard(idCard: string): Boolean {
    return infoRepo.existsAllByIdCard(idCard)
  }

  override fun groupByUserIdByUserIds(
    userIds: List<RefId>
  ): Map<RefId, List<UserInfo>> {
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
          )
          .first()
      } else null
    }
      ?: e.phone?.let { c ->
        if (infoRepo.existsAllByPhone(c)) {
          val phoneList =
            infoRepo.findAllByPhone(c).mapIndexed { index, r ->
              val d = e.fromDbData(r)
              d.apply { pri = index == 0 }
            }
          postAll(phoneList).first()
        } else null
      }
      ?: post(e.withNew())
  }

  override fun savePlainUserInfoByUser(
    createUserId: RefId,
    usr: UserAccount,
  ): UserInfo {
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
}
