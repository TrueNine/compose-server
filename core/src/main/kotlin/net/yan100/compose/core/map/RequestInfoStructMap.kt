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
package net.yan100.compose.core.map

import net.yan100.compose.core.models.AuthRequestInfo
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.MappingConstants
import org.mapstruct.factory.Mappers

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
interface RequestInfoStructMap {
  companion object {
    val INSTANCE: RequestInfoStructMap = Mappers.getMapper(RequestInfoStructMap::class.java)
  }

  @Mapping(target = "enabled", ignore = true)
  @Mapping(target = "nonLocked", ignore = true)
  @Mapping(target = "nonExpired", ignore = true)
  @Mapping(target = "encryptedPassword", ignore = true)
  fun clearAuthedInfo(info: AuthRequestInfo): AuthRequestInfo
}
