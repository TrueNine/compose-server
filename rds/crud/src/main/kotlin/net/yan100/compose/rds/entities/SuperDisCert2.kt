package net.yan100.compose.rds.entities

import com.fasterxml.jackson.annotation.JsonIgnore
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.persistence.Convert
import jakarta.persistence.MappedSuperclass
import jakarta.persistence.Transient
import net.yan100.compose.core.RefId
import net.yan100.compose.core.date
import net.yan100.compose.core.domain.IDisCode
import net.yan100.compose.core.string
import net.yan100.compose.meta.annotations.MetaDef
import net.yan100.compose.rds.converters.DisTypingConverter
import net.yan100.compose.rds.converters.GenderTypingConverter
import net.yan100.compose.rds.core.entities.IEntity
import net.yan100.compose.rds.core.typing.cert.DisTyping
import net.yan100.compose.rds.core.typing.userinfo.GenderTyping
import java.time.LocalDate

@MetaDef
@MappedSuperclass
abstract class SuperDisCert2 : IDisCode, IEntity() {

  @get:Schema(title = "用户信息id")
  abstract var userInfoId: RefId?

  @get:Schema(title = "出生日期")
  abstract var birthday: LocalDate?

  @get:Schema(title = "监护人联系电话")
  abstract var guardianPhone: String?

  @get:Schema(title = "监护人姓名")
  abstract var guardian: String?

  @get:Schema(title = "家庭住址")
  abstract var addressDetailsId: String?

  @get:Schema(title = "证件过期时间")
  abstract var expireDate: date?

  @get:Schema(title = "签发时间")
  abstract var issueDate: LocalDate?

  @get:Schema(title = "残疾级别")
  abstract var level: Int?

  @get:Schema(title = "残疾类别")
  @get:Convert(converter = DisTypingConverter::class)
  abstract var type: DisTyping

  @get:Schema(title = "性别")
  @get:Convert(converter = GenderTypingConverter::class)
  abstract var gender: GenderTyping

  @get:Schema(title = "残疾证编号")
  abstract var code: string

  @get:Schema(title = "姓名")
  abstract var name: String

  @get:Schema(title = "外联用户（所属用户）")
  abstract var userId: RefId?

  @get:Transient
  @get:JsonIgnore
  override val disCode: String
    get() = this.code

  override fun toNewEntity() {
    super.toNewEntity()
    code = code.uppercase()
  }
}
