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
package net.yan100.compose.webapidoc.autoconfig

import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.Operation
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.info.License
import io.swagger.v3.oas.models.media.Schema
import io.swagger.v3.oas.models.media.StringSchema
import io.swagger.v3.oas.models.parameters.HeaderParameter
import net.yan100.compose.core.log.slf4j
import net.yan100.compose.webapidoc.properties.SwaggerProperties
import org.springdoc.core.models.GroupedOpenApi
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.method.HandlerMethod
import java.time.LocalDateTime

@Configuration
class OpenApiDocConfig {
    companion object {
        private val log = slf4j(this::class)
    }

    @Bean
    @ConditionalOnWebApplication
    fun userApi(p: SwaggerProperties): GroupedOpenApi {
        log.debug("注册 OpenApi3 文档")
        val paths = p.scanUrlPatterns.toTypedArray()
        return GroupedOpenApi.builder()
            .group(p.group)
            .pathsToMatch(*paths)
            .packagesToScan(
                *p.scanPackages.apply { this += "net.yan100.compose" }.toTypedArray(),
            )
            .addOperationCustomizer { operation: Operation, _: HandlerMethod? ->
                if (p.enableJwtHeader) {
                    operation
                        .addParametersItem(
                            HeaderParameter()
                                .name(p.jwtHeaderInfo.authTokenName)
                                .example("eyJ0eXAiOiJ")
                                .description("jwt校验头")
                                .schema(
                                    StringSchema().name(p.jwtHeaderInfo.authTokenName).description("jwt校验头"),
                                ),
                        )
                        .addParametersItem(
                            HeaderParameter()
                                .name(p.jwtHeaderInfo.refreshTokenName)
                                .example("eyJ0eXAiOiJ")
                                .description("jwt 刷新 token")
                                .schema(
                                    StringSchema()
                                        .name(p.jwtHeaderInfo.refreshTokenName)
                                        .example("eyJ0eXAiOiJ")
                                        .description("jwt 刷新 token"),
                                ),
                        )
                } else {
                    operation
                }
            }
            .build()
    }

    @Bean
    fun customOpenApi(p: SwaggerProperties): OpenAPI {
        val authorInfo = p.authorInfo
        return OpenAPI()
            .info(
                Info()
                    .title(authorInfo.title)
                    .version(authorInfo.version)
                    .description(authorInfo.description)
                    .termsOfService(authorInfo.location)
                    .license(
                        License().name(authorInfo.license).url(authorInfo.licenseUrl),
                    ),
            )
    }
}
