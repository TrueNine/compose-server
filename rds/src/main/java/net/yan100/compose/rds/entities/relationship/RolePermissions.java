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
package net.yan100.compose.rds.entities.relationship;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import net.yan100.compose.rds.core.entities.IEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

/**
 * 角色  权限
 *
 * @author TrueNine
 * @since 2023-01-02
 */
@Getter
@Setter
@DynamicInsert
@DynamicUpdate
@Entity
@Schema(title = "角色  权限")
@Table(name = RolePermissions.TABLE_NAME)
public class RolePermissions extends IEntity {
    public static final String TABLE_NAME = "role_permissions";
    public static final String ROLE_ID = "role_id";
    public static final String PERMISSIONS_ID = "permissions_id";

    /**
     * 角色
     */
    @Nullable @Schema(title = "角色")
    @Column(name = ROLE_ID)
    private String roleId;

    /**
     * 权限
     */
    @Nullable @Schema(title = "权限")
    @Column(name = PERMISSIONS_ID)
    private String permissionsId;
}
