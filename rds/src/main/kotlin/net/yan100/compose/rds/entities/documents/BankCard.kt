package net.yan100.compose.rds.entities.documents

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.MappedSuperclass
import jakarta.persistence.Table
import net.yan100.compose.core.alias.ReferenceId
import net.yan100.compose.core.alias.SerialCode
import net.yan100.compose.rds.core.entities.TreeEntity
import org.hibernate.annotations.DynamicInsert
import org.hibernate.annotations.DynamicUpdate

@MappedSuperclass
open class SuperBankCard : TreeEntity() {
  companion object {
    const val TABLE_NAME = "bank_card"
    const val USER_ID = "user_id"
    const val CODE = "code"
    const val COUNTRY = "country"
    const val BANK_GROUP = "bank_group"
    const val BANK_TYPE = "bank_type"
    const val ISSUE_ADDRESS_DETAILS = "issue_address_details"
  }

  @Schema(title = "开户行")
  @Column(name = ISSUE_ADDRESS_DETAILS)
  open var issueAddressDetails: String? = null


  @Schema(title = "银行类型", example = "中国银行、建设银行")
  @Column(name = BANK_TYPE)
  open var bankType: String? = null

  @Schema(title = "银行组织", example = "银联")
  @Column(name = BANK_GROUP)
  open var bankGroup: Int? = null

  @Schema(title = "所属国家")
  @Column(name = COUNTRY)
  open var country: Int? = null


  @Schema(title = "银行卡号")
  @Column(name = CODE)
  open var code: SerialCode? = null

  @Column(name = USER_ID)
  open var userId: ReferenceId? = null
}

@Entity
@DynamicUpdate
@DynamicInsert
@Table(name = SuperBankCard.TABLE_NAME)
open class BankCard : SuperBankCard()
