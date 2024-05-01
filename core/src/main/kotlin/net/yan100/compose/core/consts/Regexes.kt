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
package net.yan100.compose.core.consts

/**
 * 常用正则表达式常量
 *
 * @author TrueNine
 * @since 2023-04-19
 */
@JvmDefaultWithoutCompatibility
interface Regexes {
  companion object {
    private const val `11` = "(1[1-9]|[2-9][0-9])"
    private const val `01` = "(0[1-9]|[1-9][0-9])"
    private const val year = "(19|20)\\d{2}"
    private const val month = "(0[1-9]|1[0-2])"
    private const val day = "(0[1-9]|[1-2][0-9]|3[0-1])"

    /** 中国身份证号 */
    const val CHINA_ID_CARD = "^${`11`}${`01`}${`01`}${year}${month}${day}\\d{3}[xX0-9]$"

    /** 中国残疾证号 */
    const val CHINA_DIS_CARD = "^${`11`}${`01`}${`01`}${year}${month}${day}\\d{3}[xX0-9][1-7][1-4](?:[bB][1-9])?"

    /** 英文数字账户 */
    const val ACCOUNT: String = "^[a-zA-Z0-9]+$"

    /** 密码 */
    const val PASSWORD: String = "^(?=.*\\d)(?=.*[a-zA-Z])(?=.*[^\\da-zA-Z\\s]).{1,9}$"

    /** 中国手机号手机 */
    const val CHINA_PHONE: String = "^1[2-9][1-9]\\d{8}$"
  }
}
