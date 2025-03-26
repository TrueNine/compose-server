package net.yan100.compose.rds.crud.repositories.jpa

import net.yan100.compose.core.RefId
import net.yan100.compose.rds.core.IRepo
import net.yan100.compose.rds.crud.entities.jpa.FullAddressDetails
import org.springframework.context.annotation.Primary
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository

/** # 全路径地址详情 */
@Primary
@Repository("IFullAddressDetailsRepository")
@Deprecated("关联过于复杂")
interface IFullAddressDetailsRepo : IRepo<FullAddressDetails> {
  /** ## 根据用户ID查询用户地址详情 */
  fun findAllByUserId(userId: RefId, page: Pageable): Page<FullAddressDetails>

  /** ## 根据电话查询地址 */
  fun findAllByPhone(phone: String, page: Pageable): Page<FullAddressDetails>
}
