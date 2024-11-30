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
package net.yan100.compose.rds.entities

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.persistence.MappedSuperclass
import net.yan100.compose.meta.annotations.MetaDef
import net.yan100.compose.rds.core.entities.IEntity

/**
 * # 权限
 *
 * @author TrueNine
 * @since 2023-01-02
 */
@MappedSuperclass
@MetaDef
@Schema(title = "权限")
abstract class SuperPermissions : IEntity() {
  /** 权限名 */
  @get:Schema(title = "权限名")
  abstract var name: String

  /** 权限描述 */
  @get:Schema(title = "权限描述")
  abstract var doc: String?
}
