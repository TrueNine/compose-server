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
package net.yan100.compose.rds.entities.info

import com.fasterxml.jackson.annotation.JsonBackReference
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.annotation.Nullable
import jakarta.persistence.*
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.Past
import java.time.LocalDate
import net.yan100.compose.core.alias.RefId
import net.yan100.compose.core.alias.ReferenceId
import net.yan100.compose.core.alias.SerialCode
import net.yan100.compose.core.extensionfunctions.hasText
import net.yan100.compose.core.extensionfunctions.nonText
import net.yan100.compose.core.models.IIdcard2Code
import net.yan100.compose.rds.Fk
import net.yan100.compose.rds.converters.GenderTypingConverter
import net.yan100.compose.rds.core.entities.IEntity
import net.yan100.compose.rds.core.typing.userinfo.GenderTyping
import net.yan100.compose.rds.entities.Usr
import net.yan100.compose.rds.entities.address.AddressDetails
import net.yan100.compose.rds.entities.attachment.LinkedAttachment
import org.hibernate.annotations.DynamicInsert
import org.hibernate.annotations.DynamicUpdate
import org.hibernate.annotations.NotFound
import org.hibernate.annotations.NotFoundAction

@MappedSuperclass
class SuperUserInfo : IEntity() {
  companion object {
    const val TABLE_NAME = "user_info"

    const val CREATE_USER_ID = "create_user_id"
    const val USER_ID = "user_id"
    const val AVATAR_IMG_ID = "avatar_img_id"
    const val FIRST_NAME = "first_name"
    const val LAST_NAME = "last_name"
    const val EMAIL = "email"
    const val PRI = "pri"
    const val BIRTHDAY = "birthday"
    const val ADDRESS_DETAILS_ID = "address_details_id"
    const val ADDRESS_CODE = "address_code"
    const val ADDRESS_ID = "address_id"
    const val WECHAT_ACCOUNT = "wechat_account"
    const val PHONE = "phone"
    const val ID_CARD = "id_card"
    const val GENDER = "gender"
    const val WECHAT_OPENID = "wechat_openid"
    const val WECHAT_AUTHID = "wechat_authid"
    const val QQ_OPENID = "qq_openid"
    const val QQ_ACCOUNT = "qq_account"
    const val SPARE_PHONE = "spare_phone"
  }

  @Schema(title = "创建此信息的用户") @Column(name = CREATE_USER_ID) var createUserId: ReferenceId? = null

  @Schema(title = "首选用户信息") @Column(name = PRI) var pri: Boolean? = null

  /** 用户 */
  @Schema(title = "用户") @Column(name = USER_ID) var userId: RefId? = null

  /** 用户头像 */
  @Nullable @Schema(title = "用户头像") @Column(name = AVATAR_IMG_ID) var avatarImgId: RefId? = null

  /** 姓 */
  @Nullable @Schema(title = "姓") @Column(name = FIRST_NAME) var firstName: String? = null

  /** 名 */
  @Nullable @Schema(title = "名") @Column(name = LAST_NAME) var lastName: String? = null

  /** 邮箱 */
  @Nullable @Schema(title = "邮箱") @Column(name = EMAIL) var email: @Email String? = null

  /** 生日 */
  @Nullable @Schema(title = "生日") @Column(name = BIRTHDAY) @Past var birthday: LocalDate? = null

  /** 地址 id */
  @Nullable @Schema(title = "地址 id") @Column(name = ADDRESS_DETAILS_ID) var addressDetailsId: String? = null

  @Nullable @Schema(title = "地址编码") @Column(name = ADDRESS_CODE) var addressCode: SerialCode? = null

  @Nullable @Schema(title = "地址id") @Column(name = ADDRESS_ID) var addressId: ReferenceId? = null

  @Schema(title = "qq openid") @Column(name = QQ_OPENID) var qqOpenid: ReferenceId? = null

  @Schema(title = "qq号") @Column(name = QQ_ACCOUNT) var qqAccount: ReferenceId? = null

  /** 电话号码 */
  @Nullable @Schema(title = "电话号码") @Column(name = PHONE, unique = true) var phone: String? = null

  /** 身份证 */
  @Nullable @Schema(title = "身份证") @Column(name = ID_CARD, unique = true) var idCard: String? = null

  /** 性别：0女，1难，2未知 */
  @Nullable @Schema(title = " 性别：0女，1难，2未知") @Column(name = GENDER) @Convert(converter = GenderTypingConverter::class) var gender: GenderTyping? = null

  /** 微信个人 openId */
  @Nullable @Schema(title = "微信个人 openId") @Column(name = WECHAT_OPENID) var wechatOpenid: String? = null

  @Schema(title = "微信号") @Column(name = WECHAT_ACCOUNT) var wechatAccount: SerialCode? = null

  /** 微信自定义登录id */
  @Nullable @Schema(title = "微信自定义登录id") @Column(name = WECHAT_AUTHID) var wechatAuthid: String? = null

  @Schema(title = "备用手机") @Column(name = SPARE_PHONE) var sparePhone: SerialCode? = null

  override fun asNew() {
    super.asNew()
    // 如果存在身份证，则优先采取身份证信息
    if (idCard.hasText()) {
      val idCard = IIdcard2Code.of(idCard!!)
      if (null == birthday) birthday = idCard.idcardBirthday
      if (addressCode.nonText()) addressCode = idCard.idcardDistrictCode
      if (null == gender) gender = if (idCard.idcardSex) GenderTyping.MAN else GenderTyping.WOMAN
    }
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
class UserInfo : SuperUserInfo() {
  /** 用户全名 */
  @get:Nullable
  @get:Schema(requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @get:Transient
  val fullName: String
    get() = (firstName ?: "") + (lastName ?: "")
}

/** 完全的用户信息 */
@Entity
@DynamicInsert
@DynamicUpdate
@Schema(title = "完全的用户信息")
@Table(name = SuperUserInfo.TABLE_NAME)
class FullUserInfo : SuperUserInfo() {
  companion object {
    const val MAPPED_BY_USR = "usr"
  }

  /** 连接的用户 */
  @OneToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = USER_ID, referencedColumnName = ID, foreignKey = ForeignKey(ConstraintMode.NO_CONSTRAINT), insertable = false, updatable = false)
  @JsonBackReference
  @NotFound(action = NotFoundAction.IGNORE)
  var usr: Usr? = null

  /** 用户住址 */
  @Schema(title = "用户住址", requiredMode = Schema.RequiredMode.NOT_REQUIRED, accessMode = Schema.AccessMode.READ_ONLY)
  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(
    name = ADDRESS_DETAILS_ID,
    referencedColumnName = ID,
    foreignKey = ForeignKey(ConstraintMode.NO_CONSTRAINT),
    insertable = false,
    updatable = false
  )
  @NotFound(action = NotFoundAction.IGNORE)
  var addressDetails: AddressDetails? = null

  /** 用户头像 */
  @Schema(title = "头像")
  @ManyToOne(targetEntity = LinkedAttachment::class)
  @JoinColumn(name = AVATAR_IMG_ID, referencedColumnName = ID, foreignKey = Fk(ConstraintMode.NO_CONSTRAINT), insertable = false, updatable = false)
  @NotFound(action = NotFoundAction.IGNORE)
  var avatarImage: LinkedAttachment? = null
}