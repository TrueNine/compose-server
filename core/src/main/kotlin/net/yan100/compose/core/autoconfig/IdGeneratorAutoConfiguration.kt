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
package net.yan100.compose.core.autoconfig

import net.yan100.compose.core.generator.IOrderCodeGenerator
import net.yan100.compose.core.generator.ISnowflakeGenerator
import net.yan100.compose.core.generator.SynchronizedSimpleOrderCodeGenerator
import net.yan100.compose.core.generator.SynchronizedSimpleSnowflake
import net.yan100.compose.core.properties.SnowflakeProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.DependsOn

@Configuration
@EnableConfigurationProperties(SnowflakeProperties::class)
class IdGeneratorAutoConfiguration {

  @Bean(SNOWFLAKE_BEAN_NAME)
  fun snowflake(p: SnowflakeProperties): ISnowflakeGenerator {
    return SynchronizedSimpleSnowflake(p.workId, p.dataCenterId, p.sequence, p.startTimeStamp)
  }

  @Bean
  @DependsOn(SNOWFLAKE_BEAN_NAME)
  fun bizCode(snowflake: ISnowflakeGenerator): IOrderCodeGenerator {
    return SynchronizedSimpleOrderCodeGenerator(snowflake)
  }

  companion object {
    const val SNOWFLAKE_BEAN_NAME = "snowflake_id_Generate"
  }
}
