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
package net.yan100.compose.rds.core.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.yan100.compose.core.consts.DataBaseBasicFieldNames;

import java.io.Serial;
import java.io.Serializable;

/**
 * 任意外键dao
 *
 * @author TrueNine
 * @since 2022-12-12
 */
@Setter
@Getter
@MappedSuperclass
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "任意外键类型，通常与策略模式一起使用")
public class RefEntity extends IEntity implements Serializable {

    /** 任意外键 */
    public static final String ARI = DataBaseBasicFieldNames.ANY_REFERENCE_ID;

    /** 任意类型 */
    public static final String TYP = DataBaseBasicFieldNames.ANY_REFERENCE_TYPE;

    @Serial private static final long serialVersionUID = 1L;

    @JsonIgnore
    @Column(name = ARI)
    @Schema(title = "任意外键id")
    protected Long ari;

    @JsonIgnore
    @Column(name = TYP)
    @Schema(title = "外键类别")
    protected String typ;
}
