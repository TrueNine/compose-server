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
package net.yan100.compose.rds.converters

import net.yan100.compose.core.slf4j
import net.yan100.compose.rds.RdsEntrance
import net.yan100.compose.rds.entities.DbTestPeriodConverterEntity
import net.yan100.compose.rds.repositories.DbTestPeriodConverterRepo
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest(classes = [RdsEntrance::class])
class AesEncryptConverterTest {

  @Autowired private lateinit var repo: DbTestPeriodConverterRepo
  private val log = slf4j(this::class)

  @Test
  fun bootConverter() {
    repo.save(DbTestPeriodConverterEntity())
  }
}
