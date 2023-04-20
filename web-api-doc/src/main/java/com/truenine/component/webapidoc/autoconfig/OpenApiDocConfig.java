package com.truenine.component.webapidoc.autoconfig;


import com.truenine.component.core.http.Headers;
import com.truenine.component.webapidoc.properties.SwaggerProperties;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.parameters.HeaderParameter;
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
  public GroupedOpenApi userApi(SwaggerProperties p) {
    log.debug("注册 OpenApi3 文档组件");
    String[] paths = {"/**"};
    String[] packagedToMatch = {p.getPackages()};
    return GroupedOpenApi.builder()
      .group(p.getGroup())
      .pathsToMatch(paths)
      .packagesToScan(packagedToMatch)
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
      .build();
  }

  @Bean
  public OpenAPI customOpenApi(SwaggerProperties p) {
    var authorInfo = p.getAuthorInfoProperties();
    return new OpenAPI()
      .info(new Info()
        .title(authorInfo.getTitle())
        .version(authorInfo.getVersion())
        .description(authorInfo.getDescription())
        .termsOfService(authorInfo.getGitLocation())
        .license(new License().name(authorInfo.getLicense())
          .url(authorInfo.getLicenseUrl())));
  }
}
