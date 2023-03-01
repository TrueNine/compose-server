package com.truenine.component.webapidoc.config;


import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.parameters.HeaderParameter;
import io.tn.core.api.http.Headers;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class OpenApiDocConfig {
  @Bean
  @ConditionalOnWebApplication
  public GroupedOpenApi userApi() {
    log.info("注册 OpenApi3 文档组件");
    String[] paths = {"/**"};
    String[] packagedToMatch = {"io.tn"};
    return GroupedOpenApi.builder()
      .group("default")
      .pathsToMatch(paths)
      .packagesToScan(packagedToMatch).build();
  }

  @Bean
  @ConditionalOnWebApplication
  public GroupedOpenApi info() {
    log.info("注册 OpenApi3 信息");
    String[] paths = {"/**"};
    String[] packagedToMatch = {"io.tn"};
    return GroupedOpenApi.builder()
      .group("swagger")
      .pathsToMatch(paths)
      .addOperationCustomizer(
        (operation, handlerMethod) ->
          operation
            .addParametersItem(new HeaderParameter()
              .name(Headers.AUTHORIZATION)
              .example("eyJ0eXAiOiJ")
              .description("jwt校验头")
              .schema(new StringSchema()._default("")
                .name(Headers.AUTHORIZATION)
                .description("jwt校验头")))
            .addParametersItem(
              new HeaderParameter()
                .name(Headers.X_RE_FLUSH_TOKEN)
                .example("eyJ0eXAiOiJ")
                .description("jwt 刷新 token")
                .schema(new StringSchema()
                  ._default("")
                  .name(Headers.X_RE_FLUSH_TOKEN)
                  .example("eyJ0eXAiOiJ")
                  .description("jwt 刷新 token")
                )
            ))
      .packagesToScan(packagedToMatch).build();
  }

  @Bean
  public OpenAPI customOpenApi() {
    return new OpenAPI()
      .info(new Info()
        .title("tserver")
        .version("1.0")
        .description("openAPI3 文档")
        .termsOfService("https://www.github.com/TrueNine")
        .license(new License().name("MIT License")
          .url("https://mit-license.org/")));
  }
}
