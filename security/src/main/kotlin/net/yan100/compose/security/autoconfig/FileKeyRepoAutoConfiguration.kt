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
package net.yan100.compose.security.autoconfig

import net.yan100.compose.core.slf4j
import net.yan100.compose.security.crypto.FileKeyRepo
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary

@Configuration
class FileKeyRepoAutoConfiguration {
  companion object {
    @JvmStatic
    private val log = slf4j(FileKeyRepoAutoConfiguration::class)
  }

  @Bean
  @Primary
  fun fileKeyRepo(): FileKeyRepo {
    log.debug("注册 以文件形式获取密钥")
    return FileKeyRepo()
  }
}
