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
package net.yan100.compose.depend.jvalid.test

import jakarta.validation.Valid
import net.yan100.compose.depend.jvalid.annotations.AtLeastOneNonNull
import net.yan100.compose.depend.jvalid.annotations.EntityLevelValid
import org.springframework.stereotype.Component
import org.springframework.validation.annotation.Validated

@EntityLevelValid
class AtLeast {
  @AtLeastOneNonNull var a: String? = null

  @AtLeastOneNonNull var b: String? = null

  @AtLeastOneNonNull var c: String? = null
}

@Component
@Validated
class ValidTestFns {
  fun a(@Valid a: AtLeast) {}
}