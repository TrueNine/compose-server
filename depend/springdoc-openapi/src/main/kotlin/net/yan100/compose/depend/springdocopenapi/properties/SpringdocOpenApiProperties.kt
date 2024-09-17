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
package net.yan100.compose.depend.springdocopenapi.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConfigurationPropertiesBinding
import org.springframework.boot.context.properties.NestedConfigurationProperty

private const val PREFIX = "compose.depend.springdoc-open-api"

@ConfigurationPropertiesBinding
@ConfigurationProperties(prefix = PREFIX, ignoreUnknownFields = true)
data class SpringdocOpenApiProperties (
  /**
   * 扫描的包
   */
  var scanPackages: MutableList<String> = mutableListOf(),

  /**
   * 扫描的路径
   */
  var scanUrlPatterns: List<String> = ArrayList(listOf("/**")),

  /**
   * 分组名称
   */
  var group: String = "default",

  /**
   * 开启 jwt 请求头展示
   */
  var enableJwtHeader: Boolean = false,

  /**
   * jwt 请求头信息
   */
  @NestedConfigurationProperty
  var jwtHeaderInfo: JwtHeaderInfoProperties = JwtHeaderInfoProperties(),

  /**
   * 类型定义信息
   */
  @NestedConfigurationProperty
  var authorInfo: SwaggerDescInfo = SwaggerDescInfo()
)
