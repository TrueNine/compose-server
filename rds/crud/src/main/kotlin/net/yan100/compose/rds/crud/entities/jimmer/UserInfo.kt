package net.yan100.compose.rds.crud.entities.jimmer

import net.yan100.compose.core.RefId
import net.yan100.compose.core.date
import net.yan100.compose.rds.core.entities.IJimmerEntity
import net.yan100.compose.rds.core.typing.GenderTyping
import org.babyfish.jimmer.Formula
import org.babyfish.jimmer.sql.Entity
import org.babyfish.jimmer.sql.IdView
import org.babyfish.jimmer.sql.JoinColumn
import org.babyfish.jimmer.sql.ManyToOne
import java.time.Period

/**
 * 用户信息
 */
@Entity
interface UserInfo : IJimmerEntity {
  /**
   * 所属账号
   */
  @IdView("userAccount")
  val userId: RefId?

  @ManyToOne
  @JoinColumn(name = "user_id")
  val userAccount: UserAccount?

  /**
   * 是否为主要信息
   */
  val pri: Boolean?

  /**
   * 用户头像所属附件
   */
  @ManyToOne
  @JoinColumn(name = "avatar_img_id")
  val avatarImage: Attachment?

  val firstName: String?
  val lastName: String?

  /**
   * 用户全名
   */
  @Formula(dependencies = ["firstName", "lastName"])
  val fullName: String? get() = firstName?.let { f -> lastName?.let { r -> "$f$r" } }

  val email: String?
  val birthday: date?

  /**
   * 用户当前年龄
   */
  @Formula(dependencies = ["birthday"])
  val age: Int?
    get() = birthday?.let {
      Period.between(it, date.now()).years
    }

  val phone: String?
  val sparePhone: String?
  val idCard: String?
  val gender: GenderTyping?

  val wechatOpenid: String?

  @Deprecated("已弃用")
  val wechatAccount: String?
  val wechatAuthid: String?

  val qqOpenid: String?
  val qqAccount: String?
  val addressCode: String?

  @Deprecated("已弃用，改用 addressCode")
  val addressId: RefId?
}
