package net.yan100.compose.webapidoc.autoconfig

import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.Operation
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.info.License
import io.swagger.v3.oas.models.media.StringSchema
import io.swagger.v3.oas.models.parameters.HeaderParameter
import net.yan100.compose.core.lang.slf4j
import net.yan100.compose.webapidoc.properties.SwaggerProperties
import org.springdoc.core.models.GroupedOpenApi
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
  fun userApi(p: SwaggerProperties): GroupedOpenApi {
    log.debug("注册 OpenApi3 文档")
    val paths = arrayOf("/**")
    val packagedToMatch = arrayOf(p.packages)
    return GroupedOpenApi.builder()
      .group(p.group)
      .pathsToMatch(*paths)
      .packagesToScan(*packagedToMatch)

      .addOperationCustomizer { operation: Operation, _: HandlerMethod? ->

        operation
          .addParametersItem(
            HeaderParameter()
              .name(net.yan100.compose.core.http.Headers.AUTHORIZATION)
              .example("eyJ0eXAiOiJ")
              .description("jwt校验头")
              .schema(
                StringSchema()._default("")
                  .name(net.yan100.compose.core.http.Headers.AUTHORIZATION)
                  .description("jwt校验头")
              )
          )
          .addParametersItem(
            HeaderParameter()
              .name(net.yan100.compose.core.http.Headers.X_RE_FLUSH_TOKEN)
              .example("eyJ0eXAiOiJ")
              .description("jwt 刷新 token")
              .schema(
                StringSchema()
                  ._default("")
                  .name(net.yan100.compose.core.http.Headers.X_RE_FLUSH_TOKEN)
                  .example("eyJ0eXAiOiJ")
                  .description("jwt 刷新 token")
              )
          )
      }
      .build()
  }

  @Bean
  fun customOpenApi(p: SwaggerProperties): OpenAPI {
    val authorInfo = p.authorInfoProperties
    return OpenAPI()
      .info(
        Info()
          .title(authorInfo.title)
          .version(authorInfo.version)
          .description(authorInfo.description)
          .termsOfService(authorInfo.location)
          .license(
            License().name(authorInfo.license)
              .url(authorInfo.licenseUrl)
          )
      )
  }
}
