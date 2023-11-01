package net.yan100.compose.rds.entity

import com.fasterxml.jackson.annotation.JsonBackReference
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonManagedReference
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode.NOT_REQUIRED
import jakarta.annotation.Nullable
import jakarta.persistence.*
import jakarta.persistence.ConstraintMode.NO_CONSTRAINT
import jakarta.persistence.FetchType.EAGER
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotNull
import net.yan100.compose.core.annotations.SensitiveRef
import net.yan100.compose.core.annotations.Strategy
import net.yan100.compose.core.exceptions.KnownException
import net.yan100.compose.rds.base.BaseEntity
import net.yan100.compose.rds.converters.AesEncryptConverter
import net.yan100.compose.rds.converters.typing.GenderTypingConverter
import net.yan100.compose.rds.entity.relationship.UserRoleGroup
import net.yan100.compose.rds.typing.GenderTyping
import org.hibernate.annotations.DynamicInsert
import org.hibernate.annotations.DynamicUpdate
import org.hibernate.annotations.Fetch
import org.hibernate.annotations.FetchMode.SUBSELECT
import org.hibernate.annotations.NotFound
import org.hibernate.annotations.NotFoundAction.IGNORE
import java.time.LocalDate
import java.time.LocalDateTime

@MappedSuperclass
open class SuperUser : BaseEntity() {
  /**
   * 账号
   */
  @Schema(title = "账号")
  @Column(name = ACCOUNT, nullable = false, unique = true)
  open var account: String? = null

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
  @Nullable
  @Schema(title = "密码")
  @Column(name = PWD_ENC)
  @get:SensitiveRef(Strategy.PASSWORD)
  open var pwdEnc: String? = null

  /**
   * 被封禁结束时间
   */
  @Nullable
  @JsonIgnore
  @Schema(title = "被封禁结束时间")
  @Column(name = BAN_TIME)
  open var banTime: LocalDateTime? = null

  /**
   * 最后请求时间
   */
  @Nullable
  @JsonIgnore
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

  companion object {
    const val TABLE_NAME = "user"
    const val ACCOUNT = "account"
    const val NICK_NAME = "nick_name"
    const val DOC = "doc"
    const val PWD_ENC = "pwd_enc"
    const val BAN_TIME = "ban_time"
    const val LAST_LOGIN_TIME = "last_login_time"
  }
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
@Table(name = SuperUser.TABLE_NAME)
open class User : SuperUser()

@Entity
@DynamicInsert
@DynamicUpdate
@Schema(title = "全属性用户")
@Table(name = SuperUser.TABLE_NAME)
open class FullUser : SuperUser() {
  /**
   * 角色组
   */
  @Schema(title = "角色组", requiredMode = NOT_REQUIRED)
  @ManyToMany(fetch = EAGER, targetEntity = RoleGroup::class)
  @JoinTable(
    name = UserRoleGroup.TABLE_NAME,
    joinColumns = [JoinColumn(
      name = UserRoleGroup.USER_ID,
      referencedColumnName = ID,
      foreignKey = ForeignKey(NO_CONSTRAINT),
      insertable = false,
      updatable = false
    )],
    inverseJoinColumns = [JoinColumn(
      name = UserRoleGroup.ROLE_GROUP_ID,
      referencedColumnName = ID,
      foreignKey = ForeignKey(NO_CONSTRAINT),
      insertable = false,
      updatable = false
    )],
    foreignKey = ForeignKey(NO_CONSTRAINT)
  )
  @Fetch(SUBSELECT)
  @NotFound(action = IGNORE)
  open var roleGroups: List<RoleGroup> = listOf()

  /**
   * 用户信息
   */
  @Schema(title = "用户信息", requiredMode = NOT_REQUIRED)
  @JsonManagedReference
  @OneToOne(mappedBy = FullUserInfo.MAPPED_BY_USER)
  @NotFound(action = IGNORE)
  open var info: FullUserInfo? = null
}

@MappedSuperclass
open class SuperUserInfo : BaseEntity() {
  /**
   * 用户
   */
  @Schema(title = "用户")
  @Column(name = USER_ID, nullable = false)
  open var userId: @NotNull String? = null

  /**
   * 用户头像
   */
  @Nullable
  @Schema(title = "用户头像")
  @Column(name = AVATAR_IMG_ID)
  open var avatarImgId: String? = null

  /**
   * 姓
   */
  @Nullable
  @Schema(title = "姓")
  @SensitiveRef(Strategy.NAME)
  @Column(name = FIRST_NAME)
  @Convert(converter = AesEncryptConverter::class)
  open var firstName: String? = null

  /**
   * 名
   */
  @Nullable
  @Schema(title = "名")
  @Column(name = LAST_NAME)
  open var lastName: String? = null

  /**
   * 邮箱
   */
  @Nullable
  @Schema(title = "邮箱")
  @Column(name = EMAIL)
  open var email: @Email String? = null

  /**
   * 生日
   */
  @Nullable
  @Schema(title = "生日")
  @Column(name = BIRTHDAY)
  open var birthday: LocalDate? = null

  /**
   * 地址 id
   */
  @Nullable
  @Schema(title = "地址 id")
  @Column(name = ADDRESS_DETAILS_ID)
  open var addressDetailsId: String? = null

  /**
   * 电话号码
   */
  @Nullable
  @Schema(title = "电话号码")
  @Column(name = PHONE, unique = true)
  @get:SensitiveRef(Strategy.PHONE)
  open var phone: String? = null

  /**
   * 身份证
   */
  @Nullable
  @Schema(title = "身份证")
  @Column(name = ID_CARD, unique = true)
  @get:SensitiveRef(Strategy.ID_CARD)
  open var idCard: String? = null

  /**
   * 性别：0女，1难，2未知
   */
  @Nullable
  @Schema(title = " 性别：0女，1难，2未知")
  @Column(name = GENDER)
  @Convert(converter = GenderTypingConverter::class)
  open var gender: GenderTyping? = null

  /**
   * 微信个人 openId
   */
  @Nullable
  @Schema(title = "微信个人 openId")
  @Column(name = WECHAT_OPENID)
  open var wechatOpenId: String? = null

  /**
   * 微信自定义登录id
   */
  @Nullable
  @Schema(title = "微信自定义登录id")
  @Column(name = WECHAT_AUTHID)
  open var wechastAuthid: String? = null

  companion object {
    const val TABLE_NAME = "user_info"
    const val USER_ID = "user_id"
    const val AVATAR_IMG_ID = "avatar_img_id"
    const val FIRST_NAME = "first_name"
    const val LAST_NAME = "last_name"
    const val EMAIL = "email"
    const val BIRTHDAY = "birthday"
    const val ADDRESS_DETAILS_ID = "address_details_id"
    const val PHONE = "phone"
    const val ID_CARD = "id_card"
    const val GENDER = "gender"
    const val WECHAT_OPENID = "wechat_openid"
    const val WECHAT_AUTHID = "wechat_authid"
  }
}

/**
 * 用户信息
 *
 * @author TrueNine
 * @since 2023-01-02
 */
@Entity
@DynamicInsert
@DynamicUpdate
@Schema(title = "用户信息")
@Table(name = SuperUserInfo.TABLE_NAME)
open class UserInfo : SuperUserInfo() {
  /**
   * 用户全名
   */
  @get:Schema(requiredMode = NOT_REQUIRED)
  @get:SensitiveRef(Strategy.NAME)
  @get:Transient
  @set:Transient
  open var fullName: String?
    get() = firstName + lastName
    set(_) = throw KnownException("不需要设置参数 fullPath", IllegalAccessException(), 400)
}

/**
 * 完全的用户信息
 */
@Entity
@DynamicInsert
@DynamicUpdate
@Schema(title = "完全的用户信息")
@Table(name = SuperUserInfo.TABLE_NAME)
open class FullUserInfo : SuperUserInfo() {
  /**
   * 连接的用户
   */
  @OneToOne(fetch = EAGER)
  @JoinColumn(
    name = USER_ID,
    referencedColumnName = ID,
    foreignKey = ForeignKey(NO_CONSTRAINT),
    insertable = false,
    updatable = false
  )
  @JsonBackReference
  @NotFound(action = IGNORE)
  private val user: User? = null

  /**
   * 用户住址
   */
  @Schema(title = "用户住址", requiredMode = NOT_REQUIRED, accessMode = Schema.AccessMode.READ_ONLY)
  @ManyToOne(fetch = EAGER)
  @JoinColumn(
    name = ADDRESS_DETAILS_ID,
    referencedColumnName = ID,
    foreignKey = ForeignKey(NO_CONSTRAINT),
    insertable = false,
    updatable = false
  )
  @NotFound(action = IGNORE)
  open var addressDetails: AddressDetails? = null

  /**
   * 用户头像
   */
  @Schema(title = "头像")
  @ManyToOne
  @JoinColumn(
    name = AVATAR_IMG_ID,
    referencedColumnName = ID,
    foreignKey = ForeignKey(NO_CONSTRAINT),
    insertable = false,
    updatable = false
  )
  @NotFound(action = IGNORE)
  open var avatarImage: Attachment? = null

  companion object {
    const val MAPPED_BY_USER = "user"
  }
}
