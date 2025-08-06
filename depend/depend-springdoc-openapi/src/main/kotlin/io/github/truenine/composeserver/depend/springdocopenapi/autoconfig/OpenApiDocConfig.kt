package io.github.truenine.composeserver.depend.springdocopenapi.autoconfig

import io.github.truenine.composeserver.depend.springdocopenapi.properties.SpringdocOpenApiProperties
import io.github.truenine.composeserver.slf4j
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.Operation
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.info.License
import io.swagger.v3.oas.models.media.Schema
import io.swagger.v3.oas.models.media.StringSchema
import io.swagger.v3.oas.models.parameters.HeaderParameter
import jakarta.annotation.PostConstruct
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.*
import org.springdoc.core.models.GroupedOpenApi
import org.springdoc.core.utils.SpringDocUtils
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.method.HandlerMethod

@Configuration
class OpenApiDocConfig {
  companion object {
    private val log = slf4j(this::class)
  }

  @Bean
  @ConditionalOnWebApplication
  fun userApi(p: SpringdocOpenApiProperties): GroupedOpenApi {
    log.debug("注册 OpenApi3 文档")
    val paths = p.scanUrlPatterns.toTypedArray()
    return GroupedOpenApi.builder()
      .group(p.group)
      .pathsToMatch(*paths)
      .packagesToScan(*p.scanPackages.apply { this += "net.yan100.compose" }.toTypedArray())
      .addOperationCustomizer { operation: Operation, _: HandlerMethod? ->
        if (p.enableJwtHeader) {
          operation
            .addParametersItem(
              HeaderParameter()
                .name(p.jwtHeaderInfo.authTokenName)
                .example("eyJ0eXAiOiJ")
                .description("jwt校验头")
                .schema(StringSchema().name(p.jwtHeaderInfo.authTokenName).description("jwt校验头"))
            )
            .addParametersItem(
              HeaderParameter()
                .name(p.jwtHeaderInfo.refreshTokenName)
                .example("eyJ0eXAiOiJ")
                .description("jwt 刷新 token")
                .schema(StringSchema().name(p.jwtHeaderInfo.refreshTokenName).example("eyJ0eXAiOiJ").description("jwt 刷新 token"))
            )
        } else {
          operation
        }
      }
      .build()
  }

  @Bean
  fun customOpenApi(p: SpringdocOpenApiProperties): OpenAPI {
    val authorInfo = p.authorInfo
    return OpenAPI()
      .info(
        Info()
          .title(authorInfo.title)
          .version(authorInfo.version)
          .description(authorInfo.description)
          .termsOfService(authorInfo.location)
          .license(License().name(authorInfo.license).url(authorInfo.licenseUrl))
      )
  }

  @PostConstruct
  fun postInit() {
    val cfg = SpringDocUtils.getConfig()
    val datetimeSchema =
      Schema<LocalDateTime>().apply {
        type = "string"
        format = "string"
        example = "1711971124475"
      }
    val byteArraySchema =
      Schema<ByteArray>().apply {
        type = "byte[]"
        format = "byte[]"
        example = "[1, 0, 0, 1]"
      }
    cfg.replaceWithSchema(ByteArray::class.java, byteArraySchema)

    cfg.replaceWithSchema(LocalDateTime::class.java, datetimeSchema)
    cfg.replaceWithSchema(LocalDate::class.java, datetimeSchema)
    cfg.replaceWithSchema(LocalTime::class.java, datetimeSchema)
    cfg.replaceWithSchema(Date::class.java, datetimeSchema)
  }
}
