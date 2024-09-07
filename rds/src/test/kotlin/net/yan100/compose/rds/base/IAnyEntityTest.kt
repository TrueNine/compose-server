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
package net.yan100.compose.rds.base

import net.yan100.compose.rds.entities.relationship.RolePermissions
import net.yan100.compose.rds.repositories.relationship.IRolePermissionsRepo
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@SpringBootTest
class IAnyEntityTest {
  @Autowired private lateinit var repo: IRolePermissionsRepo

  @Test
  fun `test save and update`() {
    val empty =
      RolePermissions().also {
        it.roleId = "33"
        it.permissionsId = "44"
      }
    assertTrue { empty.isNew }
    assertFalse {
      empty
        .let {
          it.id = "3344"
          it
        }
        .isNew
    }
    val b = repo.save(empty)
    assertFalse { b.isNew }
    assertTrue { b.id != null }
    val c =
      repo.save(
        b.let {
          it.roleId = "4455"
          it
        }
      )
    repo.save(c)
  }
}
