package net.yan100.compose.rds.entities.attachment

import com.fasterxml.jackson.annotation.JsonIgnore
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.persistence.Convert
import jakarta.persistence.MappedSuperclass
import jakarta.persistence.Transient
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size
import net.yan100.compose.core.alias.RefId
import net.yan100.compose.core.alias.SerialCode
import net.yan100.compose.core.alias.date
import net.yan100.compose.core.consts.Regexes
import net.yan100.compose.core.models.IDisCode2
import net.yan100.compose.ksp.core.annotations.MetaDef
import net.yan100.compose.rds.converters.DisTypingConverter
import net.yan100.compose.rds.converters.GenderTypingConverter
import net.yan100.compose.rds.core.entities.IEntity
import net.yan100.compose.rds.core.typing.cert.DisTyping
import net.yan100.compose.rds.core.typing.userinfo.GenderTyping
import java.time.LocalDate

@MetaDef
@MappedSuperclass
abstract class SuperDisCert2 : IDisCode2, IEntity() {

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

  @get:NotBlank(message = "不能为空")
  @get:Pattern(regexp = Regexes.CHINA_DIS_CARD, message = "残疾证号不合法")
  @get:Size(max = 22, min = 20, message = "残疾证长度为 20 到 22 位")
  @get:Schema(title = "残疾证编号")
  abstract var code: SerialCode

  @get:Schema(title = "姓名")
  abstract var name: String

  @get:Schema(title = "外联用户（所属用户）")
  abstract var userId: RefId?

  @get:Transient
  @get:JsonIgnore
  override val disCode: String
    get() = this.code

  override fun asNew() {
    super.asNew()
    code = code.uppercase()
  }
}
