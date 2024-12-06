package net.yan100.compose.rds.crud.entities.jimmer

import net.yan100.compose.core.RefId
import net.yan100.compose.rds.core.entities.IJimmerEntity
import org.babyfish.jimmer.sql.Entity
import org.babyfish.jimmer.sql.IdView
import org.babyfish.jimmer.sql.JoinColumn
import org.babyfish.jimmer.sql.ManyToOne

/**
 * 用户地址详情
 */
@Entity
interface AddressDetails : IJimmerEntity {
  @IdView("userAccount")
  val userId: RefId?

  /**
   * 从属账号
   */
  @ManyToOne(inputNotNull = true)
  @JoinColumn(name = "user_id")
  val userAccount: UserAccount?

  @IdView("address")
  val addressId: RefId?

  /**
   * 地址代码
   */
  val addressCode: String

  @ManyToOne
  val address: Address?

  val center: String?
}
