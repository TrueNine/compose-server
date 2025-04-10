package net.yan100.compose.rds.crud.repositories.jpa

import net.yan100.compose.rds.IRepo
import net.yan100.compose.rds.crud.entities.jpa.AddressDetails
import net.yan100.compose.rds.crud.entities.jpa.NonDesensitizedAddressDetails
import org.springframework.context.annotation.Primary
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

/**
 * # 用户地址详情服务
 *
 * @author TrueNine
 * @since 2023-06-13
 */
@Primary
@Deprecated("弃用 JPA")
@Repository("IAddressDetailsRepository")
interface IAddressDetailsRepo : IRepo<AddressDetails> {
  /** ## 根据用户ID查询用户地址详情 */
  fun findAllByUserId(userId: String, page: Pageable): Page<AddressDetails>

  /** ## 根据用户ID查询用户地址详情 */
  @Query(
    """
    FROM NonDesensitizedAddressDetails n
    WHERE n.userId = :userId
  """
  )
  fun findNonDesensitizedAllByUserId(
    userId: String,
    page: Pageable,
  ): Page<NonDesensitizedAddressDetails>

  /** ## 根据电话查询地址 */
  fun findAllByPhone(phone: String, page: Pageable): Page<AddressDetails>

  /** ## 根据电话查询非脱敏地址 */
  @Query(
    """
    FROM NonDesensitizedAddressDetails n
    WHERE n.phone = :phone
  """
  )
  fun findNonDesensitizedAllByPhone(
    phone: String,
    page: Pageable,
  ): Page<NonDesensitizedAddressDetails>
}
