package net.yan100.compose.rds.crud.entities.jpa

import net.yan100.compose.RefId
import net.yan100.compose.date
import net.yan100.compose.decimal
import net.yan100.compose.meta.annotations.MetaDef
import net.yan100.compose.rds.entities.IJpaEntity
import net.yan100.compose.string

@MetaDef
interface SuperBizCert : IJpaEntity {
  /** 所属用户信息 id */
  var userInfoId: RefId?

  /** 签发时间 */
  var issueDate: date?

  /** 地址详情 */
  var addressDetailsId: RefId?

  /** 注册地 地址代码 */
  var addressCode: string?

  /** 经营范围 */
  var bizRange: string?

  /** 法人姓名 */
  var leaderName: String?

  /** 营业执照类型 */
  var type: Int?

  /** 统一社会信用代码 */
  var uniCreditCode: string?

  /** 成立日期 */
  var createDate: date?

  /** 注册资本 */
  var regCapital: decimal?

  /** 营业执照名称 */
  var title: String

  /** 所属上传用户 */
  var userId: RefId?
}
