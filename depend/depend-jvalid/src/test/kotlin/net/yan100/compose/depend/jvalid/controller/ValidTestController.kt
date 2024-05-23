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
package net.yan100.compose.depend.jvalid.controller

import net.yan100.compose.depend.jvalid.entities.GetEntity
import net.yan100.compose.depend.jvalid.group.GetGroup
import net.yan100.compose.depend.jvalid.group.PostGroup
import net.yan100.compose.depend.jvalid.repositories.GetRepo
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("valid-test")
class ValidTestController {
  @Autowired
  lateinit var repo: GetRepo

  @GetMapping("get")
  fun getValid(@Validated(GetGroup::class) entity: GetEntity): GetEntity {
    return entity
  }

  @PostMapping("post")
  fun postValid(@Validated(PostGroup::class) entity: GetEntity): GetEntity {
    repo.save(entity)
    repo.save(GetEntity().apply { id = "1" })
    return entity
  }
}
