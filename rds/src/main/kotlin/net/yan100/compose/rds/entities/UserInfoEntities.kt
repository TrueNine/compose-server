package net.yan100.compose.rds.entities

import com.fasterxml.jackson.annotation.JsonBackReference
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.annotation.Nullable
import jakarta.persistence.*
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotNull
import net.yan100.compose.core.annotations.SensitiveRef
import net.yan100.compose.core.annotations.Strategy
import net.yan100.compose.core.exceptions.KnownException
import net.yan100.compose.rds.converters.AesEncryptConverter
import net.yan100.compose.rds.converters.GenderTypingConverter
import net.yan100.compose.rds.core.entities.BaseEntity
import net.yan100.compose.rds.typing.GenderTyping
import org.hibernate.annotations.DynamicInsert
import org.hibernate.annotations.DynamicUpdate
import org.hibernate.annotations.NotFound
import org.hibernate.annotations.NotFoundAction
import java.time.LocalDate

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
  @get:Schema(requiredMode = Schema.RequiredMode.NOT_REQUIRED)
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
  @OneToOne(fetch = FetchType.EAGER)
  @JoinColumn(
    name = USER_ID, referencedColumnName = ID, foreignKey = ForeignKey(ConstraintMode.NO_CONSTRAINT), insertable = false, updatable = false
  )
  @JsonBackReference
  @NotFound(action = NotFoundAction.IGNORE)
  private val user: User? = null

  /**
   * 用户住址
   */
  @Schema(title = "用户住址", requiredMode = Schema.RequiredMode.NOT_REQUIRED, accessMode = Schema.AccessMode.READ_ONLY)
  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(
    name = ADDRESS_DETAILS_ID, referencedColumnName = ID, foreignKey = ForeignKey(ConstraintMode.NO_CONSTRAINT), insertable = false, updatable = false
  )
  @NotFound(action = NotFoundAction.IGNORE)
  open var addressDetails: AddressDetails? = null

  /**
   * 用户头像
   */
  @Schema(title = "头像")
  @ManyToOne
  @JoinColumn(
    name = AVATAR_IMG_ID, referencedColumnName = ID, foreignKey = ForeignKey(ConstraintMode.NO_CONSTRAINT), insertable = false, updatable = false
  )
  @NotFound(action = NotFoundAction.IGNORE)
  open var avatarImage: Attachment? = null

  companion object {
    const val MAPPED_BY_USER = "user"
  }
}