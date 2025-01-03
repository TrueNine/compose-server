/*
 *  Copyright (c) 2020-2024 TrueNine. All rights reserved.
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
 *     email: <truenine304520@gmail.com>
 *     website: <github.com/TrueNine>
 */
package net.yan100.compose.rds.core.entities

private const val DEPRECATED = "该接口或实体的方法不允许调用"

/**
 * ## 字节码增强实体
 *
 * 该实体用于预留增强过的实体类，该接口的方法无需调用
 */
@Suppress("ALL")
interface IEnhanceEntity {
  /** 为自身生成 snowflake id */
  @Throws(NotImplementedError::class)
  @Deprecated(DEPRECATED, level = DeprecationLevel.HIDDEN)
  fun ____compose_rds____self_generate_snowflake_id() {
    TODO(DEPRECATED)
  }

  /** ## 为自身生成 toString 的值 */
  @Throws(NotImplementedError::class)
  @Deprecated(DEPRECATED, level = DeprecationLevel.HIDDEN)
  fun ____compose_rds____all_field_toString(): String {
    TODO(DEPRECATED)
  }

  /** 为自身生成 bizCode */
  @Throws(NotImplementedError::class)
  @Deprecated(DEPRECATED, level = DeprecationLevel.HIDDEN)
  fun ____compose_rds____self_generate_biz_code() {
    TODO(DEPRECATED)
  }
}
