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
package net.yan100.compose.core

import net.yan100.compose.core.annotations.SensitiveStrategy

interface ISensitiveScope<T> {
  fun String.addressDetails() = SensitiveStrategy.ADDRESS.desensitizeSerializer()(this)

  fun String.bankCard() = SensitiveStrategy.BANK_CARD_CODE.desensitizeSerializer()(this)

  fun String.chinaName() = SensitiveStrategy.NAME.desensitizeSerializer()(this)

  fun String.multipleName() = SensitiveStrategy.MULTIPLE_NAME.desensitizeSerializer()(this)

  fun String.chinaIdCard() = SensitiveStrategy.ID_CARD.desensitizeSerializer()(this)

  fun String.chinaPhone() = SensitiveStrategy.PHONE.desensitizeSerializer()(this)

  fun String.password() = SensitiveStrategy.PASSWORD.desensitizeSerializer()(this)

  fun String.email() = SensitiveStrategy.EMAIL.desensitizeSerializer()(this)

  fun String.once() = SensitiveStrategy.ONCE.desensitizeSerializer()(this)
}
