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
package net.yan100.compose.rds.crud.service

import io.swagger.v3.oas.annotations.media.Schema
import net.yan100.compose.core.*
import net.yan100.compose.rds.core.ICrud
import net.yan100.compose.rds.core.typing.GenderTyping
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
  fun savePlainUserInfoByUser(usr: UserAccount): UserInfo = savePlainUserInfoByUser(usr.createUserId!!, usr)

  fun findAllIdByUserId(userId: RefId): List<RefId>

  fun findUserIdById(id: RefId): RefId?

  fun findUserByWechatOpenId(openId: String): UserAccount?

  fun findUserByPhone(phone: String): UserAccount?

  fun findByUserId(userId: RefId): UserInfo?

  fun existsByPhone(phone: string): Boolean

  fun existsByWechatOpenId(openId: String): Boolean


  fun fetchAllBy(dto: UserInfoFetchParam): Pr<UserInfo>


  data class UserInfoFetchParam(
    @Schema(title = "用户id")
    val userId: RefId? = null,

    @Schema(title = "生日")
    val birthday: date? = null,

    @Schema(title = "手机号")
    val phone: String? = null,

    @Schema(title = "备用手机号")
    val sparePhone: String? = null,

    @Schema(title = "身份证号")
    val idCard: String? = null,

    @Schema(title = "性别")
    val gender: GenderTyping? = null,

    @Schema(title = "微信openId")
    val wechatOpenId: String? = null,

    @Schema(title = "微信账号")
    val wechatAccount: String? = null,

    @Schema(title = "所属地址编码")
    val addressCode: String? = null,

    @Schema(title = "用户信息id")
    val id: RefId? = null,

    @Schema(title = "备注信息")
    val remark: String? = null,

    @Schema(title = "备注名称")
    val remarkName: String? = null,

    @Schema(title = "姓氏")
    val firstName: String? = null,

    @Schema(title = "名字")
    val lastName: String? = null,

    @Schema(title = "姓名")
    val fullName: String? = null,

    @Schema(title = "创建人id")
    val createUserId: RefId? = null,

    @Schema(title = "邮箱")
    val email: String? = null,

    @Schema(title = "年龄")
    val age: Int? = null,

    @Schema(title = "是否有头像")
    val hasAvatar: Boolean? = null,
  ) : PqLike
}
