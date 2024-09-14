package net.yan100.compose.rds.repositories

import net.yan100.compose.rds.core.IRepo
import net.yan100.compose.rds.entities.FullAddressDetails
import org.springframework.context.annotation.Primary
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository

/** # 全路径地址详情 */
@Primary
@Repository
interface IFullAddressDetailsRepo : IRepo<FullAddressDetails> {
  /** ## 根据用户ID查询用户地址详情 */
  fun findAllByUserId(userId: String, page: Pageable): Page<FullAddressDetails>

  /** ## 根据电话查询地址 */
  fun findAllByPhone(phone: String, page: Pageable): Page<FullAddressDetails>
}
