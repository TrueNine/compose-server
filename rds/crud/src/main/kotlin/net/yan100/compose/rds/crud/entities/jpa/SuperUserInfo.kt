package net.yan100.compose.rds.crud.entities.jpa

import jakarta.persistence.Convert
import net.yan100.compose.core.*
import net.yan100.compose.core.domain.IIdcard2Code
import net.yan100.compose.meta.annotations.MetaDef
import net.yan100.compose.rds.core.entities.IJpaEntity
import net.yan100.compose.rds.core.typing.GenderTyping
import net.yan100.compose.rds.crud.converters.GenderTypingConverter
import java.time.LocalDate

@MetaDef
interface SuperUserInfo : IJpaEntity {
  /**
   * 备注名称
   */

  var remarkName: String?

  /**
   * 备注
   */
  var remark: String?

  /**
   * 创建此信息的用户
   */
  var createUserId: RefId?

  /**
   * 首选用户信息
   */
  var pri: Boolean?

  /** 用户 */
  var userId: RefId?

  /** 用户头像 */
  var avatarImgId: RefId?

  /** 姓 */
  var firstName: String?

  /** 名 */
  var lastName: String?

  /** 邮箱 */
  var email: String?

  /** 生日 */
  var birthday: LocalDate?

  /** 地址详情 id */
  var addressDetailsId: RefId?

  /**
   * 所在城市 代码
   *
   * 推荐使用此 code 连接地址
   */
  var addressCode: string?

  /**
   * 地址 id
   * 推荐使用地址 code
   */
  @Deprecated("推荐直接使用 code")
  var addressId: RefId?

  /**
   * qq openid
   */
  var qqOpenid: RefId?

  /**
   * qq号
   */
  var qqAccount: RefId?

  /** 电话号码 */
  var phone: String?

  /** 身份证 */
  var idCard: String?

  /** 性别 */
  @get:Convert(converter = GenderTypingConverter::class)
  var gender: GenderTyping?

  /** 微信个人 openId */
  var wechatOpenid: String?

  /**
   * 微信号
   */
  @Deprecated("不再支持微信号")
  var wechatAccount: string?

  /** 微信自定义登录id */
  var wechatAuthid: String?

  /**
   * 备用手机
   */
  var sparePhone: string?

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
