package net.yan100.compose.rds.repositories.address

import net.yan100.compose.rds.entities.address.FullAddressDetails
import net.yan100.compose.rds.repositories.base.IRepo
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository

/** # 全路径地址详情 */
@Repository
interface IFullAddressDetailsRepo : IRepo<FullAddressDetails> {
  /** ## 根据用户ID查询用户地址详情 */
  fun findAllByUserId(userId: String, page: Pageable): Page<FullAddressDetails>

  /** ## 根据电话查询地址 */
  fun findAllByPhone(phone: String, page: Pageable): Page<FullAddressDetails>
}
