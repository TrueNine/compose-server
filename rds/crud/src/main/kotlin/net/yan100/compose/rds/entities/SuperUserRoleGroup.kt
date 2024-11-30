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
import net.yan100.compose.core.RefId
import net.yan100.compose.meta.annotations.MetaDef
import net.yan100.compose.rds.core.entities.IEntity

/**
 * 用户 角色组
 *
 * @author TrueNine
 * @since 2023-01-02
 */
@MetaDef
@MappedSuperclass
@Schema(title = "用户  角色组")
abstract class SuperUserRoleGroup : IEntity() {
    /** 用户 */
    @get:Schema(title = "用户")
    abstract var userId: RefId

    /** 权限组 */
    @get:Schema(title = "权限组")
    abstract var roleGroupId: RefId
}
