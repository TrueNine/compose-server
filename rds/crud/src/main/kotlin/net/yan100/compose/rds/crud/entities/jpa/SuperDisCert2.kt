package net.yan100.compose.rds.crud.entities.jpa

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.persistence.Convert
import java.time.LocalDate
import net.yan100.compose.RefId
import net.yan100.compose.date
import net.yan100.compose.meta.annotations.MetaDef
import net.yan100.compose.rds.crud.converters.DisTypingConverter
import net.yan100.compose.rds.crud.converters.GenderTypingConverter
import net.yan100.compose.rds.entities.IJpaEntity
import net.yan100.compose.rds.typing.DisTyping
import net.yan100.compose.rds.typing.GenderTyping
import net.yan100.compose.string

@MetaDef
interface SuperDisCert2 : IJpaEntity {

  @get:Schema(title = "用户信息id") var userInfoId: RefId?

  @get:Schema(title = "出生日期") var birthday: LocalDate?

  @get:Schema(title = "监护人联系电话") var guardianPhone: String?

  @get:Schema(title = "监护人姓名") var guardian: String?

  @get:Schema(title = "家庭住址") var addressDetailsId: String?

  @get:Schema(title = "证件过期时间") var expireDate: date?

  @get:Schema(title = "签发时间") var issueDate: LocalDate?

  @get:Schema(title = "残疾级别") var level: Int?

  @get:Schema(title = "残疾类别")
  @get:Convert(converter = DisTypingConverter::class)
  var type: DisTyping

  @get:Schema(title = "性别")
  @get:Convert(converter = GenderTypingConverter::class)
  var gender: GenderTyping

  @get:Schema(title = "残疾证编号") var code: string

  @get:Schema(title = "姓名") var name: String

  @get:Schema(title = "外联用户（所属用户）") var userId: RefId?

  override fun toNewEntity() {
    super.toNewEntity()
    code = code.uppercase()
  }
}
