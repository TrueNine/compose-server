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
package net.yan100.compose.rds.repositories

import jakarta.annotation.Resource
import net.yan100.compose.rds.entities.DbTestDurationConverterEntity
import net.yan100.compose.testtookit.log
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.Rollback
import java.time.Duration
import kotlin.test.assertNotNull

@Rollback
@SpringBootTest
class DbTestDurationConverterRepoTest {

  lateinit var repo: DbTestDurationConverterRepo @Resource set

  @Test
  @Rollback
  fun testSaveAndFind() {
    val entity = DbTestDurationConverterEntity().apply { durations = Duration.parse("PT24H") }
    entity.durations = Duration.parse("PT24H")
    val saved = repo.save(entity)
    assertNotNull(saved)
    log.info("saved = {}", saved)
  }
}
