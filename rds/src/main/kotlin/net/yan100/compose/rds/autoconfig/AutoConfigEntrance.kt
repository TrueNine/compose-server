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
package net.yan100.compose.rds.autoconfig

import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.context.annotation.ComponentScan
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@EnableJpaAuditing
@EntityScan(
  basePackages = [
    "net.yan100.compose.rds.entities",
    "net.yan100.compose.rds.models",
    "net.yan100.compose.rds.core"
  ]
)
@ComponentScan(
  "net.yan100.compose.rds.autoconfig",
  "net.yan100.compose.rds.converters",
  "net.yan100.compose.rds.service",
  "net.yan100.compose.rds.repositories",
  "net.yan100.compose.rds.listener",
)
@EnableJpaRepositories("net.yan100.compose.rds.repositories")
class AutoConfigEntrance
