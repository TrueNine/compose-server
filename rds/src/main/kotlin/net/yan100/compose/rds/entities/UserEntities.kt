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
import net.yan100.compose.core.alias.ReferenceId
import net.yan100.compose.core.alias.SerialCode
import net.yan100.compose.core.consts.Regexes
import net.yan100.compose.core.exceptions.KnownException
import net.yan100.compose.rds.Oto
import net.yan100.compose.rds.core.entities.BaseEntity
import net.yan100.compose.rds.entities.relationship.UserRoleGroup
import org.hibernate.annotations.DynamicInsert
import org.hibernate.annotations.DynamicUpdate
import org.hibernate.annotations.Fetch
import org.hibernate.annotations.FetchMode.SUBSELECT
import org.hibernate.annotations.NotFound
import org.hibernate.annotations.NotFoundAction.IGNORE
import java.time.LocalDateTime

@MappedSuperclass
open class SuperUsr : BaseEntity() {
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

  /**
   * 创建此账号的 user id
   */
  @Schema(title = "创建此账号的 user id")
  @Column(name = CREATE_USER_ID)
  open var createUserId: ReferenceId? = null

  /**
   * 账号
   */
  @NotEmpty
  @Size(min = 4, max = 256)
  @Schema(title = "账号")
  @Pattern(regexp = Regexes.ACCOUNT)
  @Column(name = ACCOUNT, unique = true)
  open var account: SerialCode? = null

  /**
   * 呢称
   */
  @Nullable
  @Schema(title = "呢称")
  @Column(name = NICK_NAME)
  open var nickName: String? = null

  /**
   * 描述
   */
  @Nullable
  @Schema(title = "描述")
  @Column(name = DOC)
  open var doc: String? = null

  /**
   * 密码
   */
  @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
  @Size(min = 8)
  @Schema(title = "密码")
  @Column(name = PWD_ENC)
  open var pwdEnc: String? = null

  /**
   * 被封禁结束时间
   */
  @Nullable
  @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
  @FutureOrPresent
  @Schema(title = "被封禁结束时间")
  @Column(name = BAN_TIME)
  open var banTime: LocalDateTime? = null

  /**
   * 最后请求时间
   */
  @Nullable
  @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
  @Schema(title = "最后请求时间")
  @Column(name = LAST_LOGIN_TIME)
  open var lastLoginTime: LocalDateTime? = null

  /**
   * @return 当前用户是否被封禁
   */
  @get:Schema(requiredMode = NOT_REQUIRED)
  @get:Transient
  @set:Transient
  var band: Boolean?
    get() = (null != banTime && LocalDateTime.now().isBefore(banTime))
    set(_) = throw KnownException("属性为不可调用", IllegalAccessException(), 400)
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
open class Usr : SuperUsr()

@Entity
@DynamicInsert
@DynamicUpdate
@Schema(title = "全属性用户")
@Table(name = SuperUsr.TABLE_NAME)
open class FullUsr : SuperUsr() {
  /**
   * 角色组
   */
  @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
  @Schema(title = "角色组", requiredMode = NOT_REQUIRED)
  @ManyToMany(fetch = EAGER, targetEntity = RoleGroup::class)
  @JoinTable(
    name = UserRoleGroup.TABLE_NAME, joinColumns = [JoinColumn(
      name = UserRoleGroup.USER_ID,
      referencedColumnName = ID,
      foreignKey = ForeignKey(NO_CONSTRAINT),
      insertable = false,
      updatable = false
    )], inverseJoinColumns = [JoinColumn(
      name = UserRoleGroup.ROLE_GROUP_ID,
      referencedColumnName = ID,
      foreignKey = ForeignKey(NO_CONSTRAINT),
      insertable = false,
      updatable = false
    )], foreignKey = ForeignKey(NO_CONSTRAINT)
  )
  @Fetch(SUBSELECT)
  @NotFound(action = IGNORE)
  open var roleGroups: List<RoleGroup> = listOf()

  /**
   * 用户信息
   */
  @Schema(title = "用户信息", requiredMode = NOT_REQUIRED)
  @JsonManagedReference
  @Oto(mappedBy = FullUserInfo.MAPPED_BY_USR)
  @NotFound(action = IGNORE)
  open var info: FullUserInfo? = null
}
