package net.yan100.compose.rds.crud.entities.jpa

import jakarta.persistence.Transient
import net.yan100.compose.core.RefId
import net.yan100.compose.core.datetime
import net.yan100.compose.core.sensitiveAlso
import net.yan100.compose.core.string
import net.yan100.compose.meta.annotations.MetaDef
import net.yan100.compose.meta.annotations.MetaSkipGeneration
import net.yan100.compose.meta.annotations.orm.MetaFormula
import net.yan100.compose.rds.core.entities.IJpaEntity

@MetaDef
interface SuperUserAccount : IJpaEntity {
  /** 创建此账号的 user id */
  var createUserId: RefId?

  /** 账号 */
  var account: string

  /** 呢称 */
  var nickName: String?

  /** 描述 */
  var doc: String?

  /** 密码 */
  var pwdEnc: String

  /** 被封禁结束时间 */
  var banTime: datetime?

  /** 最后请求时间 */
  var lastLoginTime: datetime?

  /** @return 当前用户是否被封禁 */
  @get:Transient
  @MetaSkipGeneration
  @MetaFormula
  val band: Boolean get() = (null != banTime && datetime.now().isBefore(banTime))

  override fun changeWithSensitiveData() {
    super.changeWithSensitiveData()
    sensitiveAlso(this) {
      it.pwdEnc = it.pwdEnc.password()
    }
    recordChangedSensitiveData()
  }
}
