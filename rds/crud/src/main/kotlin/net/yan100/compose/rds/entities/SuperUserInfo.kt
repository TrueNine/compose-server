package net.yan100.compose.rds.entities

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.persistence.Convert
import jakarta.persistence.MappedSuperclass
import net.yan100.compose.core.*
import net.yan100.compose.core.domain.IIdcard2Code
import net.yan100.compose.meta.annotations.MetaDef
import net.yan100.compose.rds.converters.GenderTypingConverter
import net.yan100.compose.rds.core.entities.IEntity
import net.yan100.compose.rds.core.typing.userinfo.GenderTyping
import java.time.LocalDate

@MetaDef
@MappedSuperclass
abstract class SuperUserInfo : IEntity() {

  @get:Schema(title = "备注名称")
  abstract var remarkName: String?

  @get:Schema(title = "备注")
  abstract var remark: String?

  @get:Schema(title = "创建此信息的用户")
  abstract var createUserId: RefId?

  @get:Schema(title = "首选用户信息")
  abstract var pri: Boolean?

  /** 用户 */
  @get:Schema(title = "用户")
  abstract var userId: RefId?

  /** 用户头像 */
  @get:Schema(title = "用户头像")
  abstract var avatarImgId: RefId?

  /** 姓 */
  @get:Schema(title = "姓")
  abstract var firstName: String?

  /** 名 */
  @get:Schema(title = "名")
  abstract var lastName: String?

  /** 邮箱 */
  @get:Schema(title = "邮箱")
  abstract var email: String?

  /** 生日 */
  @get:Schema(title = "生日")
  abstract var birthday: LocalDate?

  /** 地址 id */
  @Deprecated("弃用地址 id")
  @get:Schema(title = "地址 id")
  abstract var addressDetailsId: String?

  @get:Schema(title = "地址编码")
  abstract var addressCode: string?

  @get:Schema(title = "地址id")
  abstract var addressId: RefId?

  @get:Schema(title = "qq openid")
  abstract var qqOpenid: RefId?

  @get:Schema(title = "qq号")
  abstract var qqAccount: RefId?

  /** 电话号码 */
  @get:Schema(title = "电话号码")
  abstract var phone: String?

  /** 身份证 */
  @get:Schema(title = "身份证")
  abstract var idCard: String?

  /** 性别：0女，1难，2未知 */
  @get:Schema(title = " 性别")
  @get:Convert(converter = GenderTypingConverter::class)
  abstract var gender: GenderTyping?

  /** 微信个人 openId */
  @get:Schema(title = "微信个人 openId")
  abstract var wechatOpenid: String?

  @Deprecated("不再支持微信号")
  @get:Schema(title = "微信号")
  abstract var wechatAccount: string?

  /** 微信自定义登录id */
  @get:Schema(title = "微信自定义登录id")
  abstract var wechatAuthid: String?

  @get:Schema(title = "备用手机")
  abstract var sparePhone: string?

  override fun toNewEntity() {
    super.toNewEntity()
    // 如果存在身份证，则优先采取身份证信息
    if (idCard.hasText()) {
      val i = IIdcard2Code[idCard!!]
      if (null == birthday) birthday = i.idcardBirthday
      if (addressCode.nonText()) addressCode = i.idcardDistrictCode
      if (null == gender) gender = if (i.idcardSex) GenderTyping.MAN else GenderTyping.WOMAN
    }
  }

  override fun changeWithSensitiveData() {
    super.changeWithSensitiveData()
    sensitiveAlso(this) {
      it.firstName = firstName?.once()
      it.lastName = lastName?.chinaName()
      it.idCard = idCard?.chinaIdCard()
      it.email = email?.email()
      it.phone = phone?.chinaPhone()
      it.sparePhone = sparePhone?.chinaPhone()
    }
    recordChangedSensitiveData()
  }
}
