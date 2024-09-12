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
package net.yan100.compose.depend.webservlet.properties

import lombok.Data
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.http.converter.StringHttpMessageConverter
import kotlin.reflect.KClass

/**
 * spring web mvc http servlet 配置属性
 *
 * @author TrueNine
 * @since 2023-02-20
 */
@Data
@ConfigurationProperties(prefix = "compose.web-servlet")
class ServletWebApplicationProperties {
  var allowConverters = mutableListOf("getDocumentation", "swaggerResources", "openapiJson")
  var allowConverterClasses = mutableListOf<KClass<*>>(StringHttpMessageConverter::class)
}
