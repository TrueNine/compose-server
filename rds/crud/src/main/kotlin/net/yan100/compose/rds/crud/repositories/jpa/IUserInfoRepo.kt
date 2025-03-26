package net.yan100.compose.rds.crud.repositories.jpa

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import net.yan100.compose.core.Pq
import net.yan100.compose.core.RefId
import net.yan100.compose.rds.core.IRepo
import net.yan100.compose.rds.core.annotations.ACID
import net.yan100.compose.rds.core.toPageable
import net.yan100.compose.rds.core.toPr
import net.yan100.compose.rds.crud.entities.jpa.UserAccount
import net.yan100.compose.rds.crud.entities.jpa.UserInfo
import org.springframework.context.annotation.Primary
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Primary
@Deprecated("弃用 JPA")
@Repository("IUserInfoRepository")
interface IUserInfoRepo : IRepo<UserInfo> {
  fun existsAllByFirstNameAndLastName(
    firstName: String,
    lastName: String,
  ): Boolean

  fun findAllByUserId(userId: RefId): List<UserInfo>

  fun findAllByUserId(userId: RefId, page: Pageable): Page<UserInfo>

  fun findAllByUserId(userId: RefId, pq: Pq) =
    findAllByUserId(userId, pq.toPageable()).toPr()

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
    join UserAccount u on u.id = i.userId
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

  @Deprecated("可会查询出多个用户")
  fun findByUserId(userId: RefId): UserInfo?

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
    from UserAccount u
    left join UserInfo i ON u.id = i.userId
    where i.wechatOpenid = :openid
    """
  )
  fun findUserByWechatOpenId(openid: String): UserAccount?

  /** 根据 电话号码查询用户手机号 */
  @Query(
    """
    from UserAccount u
    left join UserInfo i on u.id = i.userId
    where i.phone = :phone
  """
  )
  fun findUserByPhone(phone: String): UserAccount?

  fun existsByPhone(phone: String): Boolean

  fun existsByWechatOpenid(wechatOpenId: String): Boolean

  @ACID
  fun deleteByPhone(phone: String): Int
}
