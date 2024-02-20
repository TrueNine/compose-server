/*
 * ## Copyright (c) 2024 TrueNine. All rights reserved.
 *
 * The following source code is owned, developed and copyrighted by TrueNine
 * (truenine304520@gmail.com) and represents a substantial investment of time, effort,
 * and resources. This software and its components are not to be used, reproduced,
 * distributed, or sublicensed in any form without the express written consent of
 * the copyright owner, except as permitted by law.
 * Any unauthorized use, distribution, or modification of this source code,
 * or any portion thereof, may result in severe civil and criminal penalties,
 * and will be prosecuted to the maximum extent possible under the law.
 * For inquiries regarding usage or redistribution, please contact:
 *     TrueNine
 *     Email: <truenine304520@gmail.com>
 *     Website: [gitee.com/TrueNine]
 */
package net.yan100.compose.rds.repositories.address

import net.yan100.compose.rds.entities.AddressDetails
import net.yan100.compose.rds.entities.FullAddressDetails
import net.yan100.compose.rds.entities.NonDesensitizedAddressDetails
import net.yan100.compose.rds.repositories.base.IRepo
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
@Repository
interface IAddressDetailsRepo : IRepo<AddressDetails> {
  /** ## 根据用户ID查询用户地址详情 */
  fun findAllByUserId(userId: String, page: Pageable): Page<AddressDetails>

  /** ## 根据用户ID查询用户地址详情 */
  @Query("""
    FROM NonDesensitizedAddressDetails n
    WHERE n.userId = :userId
  """)
  fun findNonDesensitizedAllByUserId(
    userId: String,
    page: Pageable
  ): Page<NonDesensitizedAddressDetails>

  /** ## 根据电话查询地址 */
  fun findAllByPhone(phone: String, page: Pageable): Page<AddressDetails>

  /** ## 根据电话查询非脱敏地址 */
  @Query("""
    FROM NonDesensitizedAddressDetails n
    WHERE n.phone = :phone
  """)
  fun findNonDesensitizedAllByPhone(
    phone: String,
    page: Pageable
  ): Page<NonDesensitizedAddressDetails>
}

/** # 全路径地址详情 */
@Repository
interface FullAddressDetailsRepo : IRepo<FullAddressDetails> {
  /** ## 根据用户ID查询用户地址详情 */
  fun findAllByUserId(userId: String, page: Pageable): Page<FullAddressDetails>

  /** ## 根据电话查询地址 */
  fun findAllByPhone(phone: String, page: Pageable): Page<FullAddressDetails>
}
