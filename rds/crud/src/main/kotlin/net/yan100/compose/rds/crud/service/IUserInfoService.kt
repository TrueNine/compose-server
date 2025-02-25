package net.yan100.compose.rds.crud.service

import net.yan100.compose.core.RefId
import net.yan100.compose.core.string
import net.yan100.compose.rds.core.ICrud
import net.yan100.compose.rds.crud.entities.jpa.UserAccount
import net.yan100.compose.rds.crud.entities.jpa.UserInfo

interface IUserInfoService : ICrud<UserInfo> {
  suspend fun findIsRealPeopleById(id: RefId): Boolean

  suspend fun findIsRealPeopleByUserId(userId: RefId): Boolean

  fun existsByFirstNameAndLastName(firstName: String, lastName: String): Boolean

  fun existsByIdCard(idCard: string): Boolean

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
  fun savePlainUserInfoByUser(createUserId: RefId, usr: UserAccount): UserInfo

  /**
   * ## 根据用户，保存一个默认的主要用户信息
   *
   * createUserId 根据当前用户的 createUserId 来计算
   *
   * @param usr 用户
   * @see [savePlainUserInfoByUser]
   */
  fun savePlainUserInfoByUser(usr: UserAccount): UserInfo =
    savePlainUserInfoByUser(usr.createUserId!!, usr)

  fun findAllIdByUserId(userId: RefId): List<RefId>

  fun findUserIdById(id: RefId): RefId?

  fun findUserByWechatOpenId(openId: String): UserAccount?

  fun findUserByPhone(phone: String): UserAccount?

  fun findByUserId(userId: RefId): UserInfo?

  fun existsByPhone(phone: string): Boolean

  fun existsByWechatOpenId(openId: String): Boolean
}
