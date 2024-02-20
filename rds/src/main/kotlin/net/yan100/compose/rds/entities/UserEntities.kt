/*
 * ## Copyright (c) 2024 TrueNine. All rights reserved.
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
 *     Email: <truenine304520@gmail.com>
 *     Website: [gitee.com/TrueNine]
 */
package net.yan100.compose.rds.entities

import com.fasterxml.jackson.annotation.JsonManagedReference
import com.fasterxml.jackson.annotation.JsonProperty
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode.NOT_REQUIRED
import jakarta.annotation.Nullable
import jakarta.persistence.*
import jakarta.persistence.ConstraintMode.NO_CONSTRAINT
import jakarta.persistence.FetchType.EAGER
import jakarta.validation.constraints.FutureOrPresent
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size
import java.time.LocalDateTime
import net.yan100.compose.core.alias.RefId
import net.yan100.compose.core.alias.SerialCode
import net.yan100.compose.core.alias.datetime
import net.yan100.compose.core.consts.Regexes
import net.yan100.compose.rds.Oto
import net.yan100.compose.rds.core.entities.IEntity
import net.yan100.compose.rds.entities.relationship.UserRoleGroup
import org.hibernate.annotations.DynamicInsert
import org.hibernate.annotations.DynamicUpdate
import org.hibernate.annotations.Fetch
import org.hibernate.annotations.FetchMode.SUBSELECT
import org.hibernate.annotations.NotFound
import org.hibernate.annotations.NotFoundAction.IGNORE

@MappedSuperclass
abstract class SuperUsr : IEntity() {
  companion object {
    const val TABLE_NAME = "usr"

    const val ACCOUNT = "account"
    const val NICK_NAME = "nick_name"
    const val DOC = "doc"
    const val PWD_ENC = "pwd_enc"
    const val BAN_TIME = "ban_time"
    const val LAST_LOGIN_TIME = "last_login_time"
    const val CREATE_USER_ID = "create_user_id"
  }

  /** 创建此账号的 user id */
  @Schema(title = "创建此账号的 user id") @Column(name = CREATE_USER_ID) lateinit var createUserId: RefId

  /** 账号 */
  @NotEmpty
  @Size(min = 4, max = 256)
  @Schema(title = "账号")
  @Pattern(regexp = Regexes.ACCOUNT)
  @Column(name = ACCOUNT, unique = true)
  lateinit var account: SerialCode

  /** 呢称 */
  @Nullable @Schema(title = "呢称") @Column(name = NICK_NAME) var nickName: String? = null

  /** 描述 */
  @Nullable @Schema(title = "描述") @Column(name = DOC) var doc: String? = null

  /** 密码 */
  @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
  @Size(min = 8)
  @Schema(title = "密码")
  @Column(name = PWD_ENC)
  lateinit var pwdEnc: String

  /** 被封禁结束时间 */
  @Nullable
  @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
  @FutureOrPresent
  @Schema(title = "被封禁结束时间")
  @Column(name = BAN_TIME)
  var banTime: datetime? = null

  /** 最后请求时间 */
  @Nullable
  @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
  @Schema(title = "最后请求时间")
  @Column(name = LAST_LOGIN_TIME)
  var lastLoginTime: datetime? = null

  /** @return 当前用户是否被封禁 */
  @get:Schema(requiredMode = NOT_REQUIRED)
  @get:Transient
  val band: Boolean
    get() = (null != banTime && LocalDateTime.now().isBefore(banTime))
}

/**
 * 用户
 *
 * @author TrueNine
 * @since 2023-01-02
 */
@Entity
@DynamicInsert
@DynamicUpdate
@Schema(title = "用户")
@Table(name = SuperUsr.TABLE_NAME)
class Usr : SuperUsr()

@Entity
@DynamicInsert
@DynamicUpdate
@Schema(title = "全属性用户")
@Table(name = SuperUsr.TABLE_NAME)
class FullUsr : SuperUsr() {
  /** 角色组 */
  @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
  @Schema(title = "角色组", requiredMode = NOT_REQUIRED)
  @ManyToMany(fetch = EAGER, targetEntity = RoleGroup::class)
  @JoinTable(
    name = UserRoleGroup.TABLE_NAME,
    joinColumns =
      [
        JoinColumn(
          name = UserRoleGroup.USER_ID,
          referencedColumnName = ID,
          foreignKey = ForeignKey(NO_CONSTRAINT),
          insertable = false,
          updatable = false
        )
      ],
    inverseJoinColumns =
      [
        JoinColumn(
          name = UserRoleGroup.ROLE_GROUP_ID,
          referencedColumnName = ID,
          foreignKey = ForeignKey(NO_CONSTRAINT),
          insertable = false,
          updatable = false
        )
      ],
    foreignKey = ForeignKey(NO_CONSTRAINT)
  )
  @Fetch(SUBSELECT)
  @NotFound(action = IGNORE)
  var roleGroups: List<RoleGroup> = mutableListOf()

  /** 用户信息 */
  @Schema(title = "用户信息", requiredMode = NOT_REQUIRED)
  @JsonManagedReference
  @Oto(mappedBy = FullUserInfo.MAPPED_BY_USR)
  @NotFound(action = IGNORE)
  var info: FullUserInfo? = null
}
