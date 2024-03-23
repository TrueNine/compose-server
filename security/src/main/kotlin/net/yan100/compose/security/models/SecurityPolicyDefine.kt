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
package net.yan100.compose.security.models

import net.yan100.compose.security.spring.security.SecurityExceptionAdware
import net.yan100.compose.security.spring.security.SecurityPreflightValidFilter
import net.yan100.compose.security.spring.security.SecurityUserDetailsService

/**
 * spring security 安全策略配置
 *
 * @author TrueNine
 * @since 2022-12-10
 */
class SecurityPolicyDefine {
  var preValidFilter: SecurityPreflightValidFilter? = null
  var service: SecurityUserDetailsService? = null
  var exceptionAdware: SecurityExceptionAdware? = null
  var anonymousPatterns: MutableList<String> = ArrayList()
  var swaggerPatterns: MutableList<String> = ArrayList(listOf("/v3/api-docs/**", "/v3/api-docs.yaml", "/doc.html**", "/swagger-ui/**"))
}
