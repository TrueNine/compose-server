package net.yan100.compose.rds.entities

import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode.NOT_REQUIRED
import jakarta.persistence.MappedSuperclass
import jakarta.persistence.Transient
import jakarta.validation.constraints.*
import net.yan100.compose.core.RefId
import net.yan100.compose.core.consts.IRegexes
import net.yan100.compose.core.datetime
import net.yan100.compose.core.sensitiveAlso
import net.yan100.compose.core.string
import net.yan100.compose.ksp.core.annotations.MetaDef
import net.yan100.compose.rds.core.entities.IEntity

@MetaDef
@MappedSuperclass
abstract class SuperUsr : IEntity() {
  /** 创建此账号的 user id */
  @get:Schema(title = "创建此账号的 user id")
  abstract var createUserId: RefId?

  /** 账号 */
  @get:NotEmpty(message = "账号不能为空")
  @get:Size(min = 4, max = 256, message = "账号长度不对")
  @get:Schema(title = "账号")
  @get:Pattern(regexp = IRegexes.ACCOUNT)
  abstract var account: string

  /** 呢称 */
  @get:Schema(title = "呢称")
  @get:NotBlank(message = "呢称不能为空")
  abstract var nickName: String?

  /** 描述 */
  @get:Schema(title = "描述")
  abstract var doc: String?

  /** 密码 */
  @get:Size(min = 8, message = "密码至少 8 位")
  @get:Schema(title = "密码")
  abstract var pwdEnc: String

  /** 被封禁结束时间 */
  @get:FutureOrPresent(message = "生日不正确")
  @get:Schema(title = "被封禁结束时间")
  abstract var banTime: datetime?

  /** 最后请求时间 */
  @get:Schema(title = "最后请求时间")
  abstract var lastLoginTime: datetime?

  /** @return 当前用户是否被封禁 */
  @get:Schema(requiredMode = NOT_REQUIRED)
  @get:Transient
  val band: Boolean get() = (null != banTime && datetime.now().isBefore(banTime))

  override fun changeWithSensitiveData() {
    sensitiveAlso(this) { it.pwdEnc = it.pwdEnc.password() }
  }
}
