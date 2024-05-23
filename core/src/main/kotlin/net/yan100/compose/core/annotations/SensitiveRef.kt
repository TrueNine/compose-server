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
package net.yan100.compose.core.annotations

import com.fasterxml.jackson.annotation.JacksonAnnotationsInside
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import java.lang.annotation.Inherited
import net.yan100.compose.core.extensionfunctions.nonText
import net.yan100.compose.core.jackson.SensitiveSerializer

/**
 * 脱敏字段检查器，通常标记于字段。 配合 Strategy 里面的常用规则使用
 *
 * @author TrueNine
 * @since 2023-02-19
 */
@Inherited
@JsonInclude
@MustBeDocumented
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.PROPERTY_GETTER)
@JacksonAnnotationsInside
@JsonSerialize(using = SensitiveSerializer::class)
annotation class SensitiveRef(val value: Strategy = Strategy.NONE) {
  enum class Strategy(private val desensitizeSerializer: (String) -> String) {
    /** 不进行脱敏处理 */
    NONE({ it }),

    /** 手机号 */
    PHONE({ it.replace("^(\\S{3})\\S+(\\S{2})$".toRegex(), "\$1********\$2") }),
    EMAIL({ it.replace("(\\S{2})\\S+(@[\\w.-]+)".toRegex(), "\$1****\$2") }),

    /** 身份证号 */
    ID_CARD({ it.replace("(\\S{2})\\S+(\\S{2})".toRegex(), "\$1****\$2") }),

    /** 银行卡号 */
    BANK_CARD_CODE({ it.replace("(\\w{2})\\w+(\\w{2})".toRegex(), "\$1****\$2") }),

    /** 姓名 */
    NAME({ if (it.nonText() || it.length < 2) it else "**${it.substring(it.length - 1)}" }),

    /** 地址 */
    ADDRESS({ it.replace("(\\S{3})\\S{2}(\\S*)\\S{2}".toRegex(), "\$1****\$2") }),

    /** 密码 */
    PASSWORD({ "****" });

    open fun desensitizeSerializer(): (String) -> String {
      return desensitizeSerializer
    }
  }
}
