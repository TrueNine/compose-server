package net.yan100.compose.rds.entities.cert

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.MappedSuperclass
import jakarta.persistence.Table
import net.yan100.compose.core.alias.BigText
import net.yan100.compose.core.alias.RefId
import net.yan100.compose.core.alias.ReferenceId
import net.yan100.compose.core.alias.SerialCode
import net.yan100.compose.rds.core.entities.BaseEntity
import org.hibernate.annotations.DynamicInsert
import org.hibernate.annotations.DynamicUpdate
import java.math.BigDecimal
import java.time.LocalDate

@MappedSuperclass
open class SuperBizCert : BaseEntity() {
  companion object {
    const val TABLE_NAME = "biz_cert"

    const val TYPE = "type"
    const val TITLE = "title"
    const val USER_ID = "user_id"
    const val USER_INFO_ID = "user_info_id"
    const val REG_CAPITAL = "reg_capital"
    const val CREATE_DATE = "create_date"
    const val BIZ_RANGE = "biz_range"
    const val ISSUE_DATE = "issue_date"
    const val LEADER_NAME = "leader_name"
    const val ADDRESS_CODE = "address_code"
    const val UNI_CREDIT_CODE = "uni_credit_code"
    const val ADDRESS_DETAILS_ID = "address_details_id"
  }

  @Schema(title = "用户信息id")
  @Column(name = USER_INFO_ID)
  open var userInfoId: RefId? = null

  @Schema(title = "签发时间")
  @Column(name = ISSUE_DATE)
  open var issueDate: LocalDate? = null


  @Schema(title = "地址详情")
  @Column(name = ADDRESS_DETAILS_ID)
  open var addressDetailsId: ReferenceId? = null

  @Schema(title = "注册地")
  @Column(name = ADDRESS_CODE)
  open var addressCode: SerialCode? = null

  @Schema(title = "经营范围")
  @Column(name = BIZ_RANGE)
  open var bizRange: BigText? = null

  @Schema(title = "法人姓名")
  @Column(name = LEADER_NAME)
  open var leaderName: String? = null

  @Schema(title = "类型")
  @Column(name = TYPE)
  open var type: Int? = null

  @Schema(title = "统一社会信用代码")
  @Column(name = UNI_CREDIT_CODE)
  open var uniCreditCode: SerialCode? = null

  @Schema(title = "成立日期")
  @Column(name = CREATE_DATE)
  open var createDate: LocalDate? = null

  @Schema(title = "注册资本")
  @Column(name = REG_CAPITAL)
  open var regCapital: BigDecimal? = null

  @Schema(title = "营业执照名称")
  @Column(name = TITLE)
  open var title: String? = null

  @Schema(title = "所属上传用户")
  @Column(name = USER_ID)
  open var userId: ReferenceId? = null
}


@Entity
@DynamicUpdate
@DynamicInsert
@Table(name = SuperBizCert.TABLE_NAME)
open class BizCert : SuperBizCert()
