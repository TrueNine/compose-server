package com.truenine.component.webapidoc.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.truenine.component.webapidoc.models.resp.OpenApiDocResponseResult;
import com.truenine.component.webapidoc.models.resp.OpenApiUrlsResponseResult;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * OpenApi3 适配器
 * /@RequestMapping("/v3/api-docs")
 *
 * @author TrueNine
 * @since 2022-12-10
 */
@Slf4j
@RestController
@RequestMapping("/v3/api-docs")
public class OpenApiConfigController {

  final ObjectMapper mapper;
  final List<GroupedOpenApi> apis;

  public OpenApiConfigController(ObjectMapper mapper, List<GroupedOpenApi> apis) {
    this.mapper = mapper;
    this.apis = apis;
  }

  @Operation(ignoreJsonView = true, hidden = true)
  @GetMapping(value = "swagger-config", produces = "application/json")
  String config() throws JsonProcessingException {
    var result = new OpenApiDocResponseResult();
    log.debug("请求 api 文档 swagger-config");
    log.debug("apis = {}", this.apis);
    result.setConfigUrl("/v3/swagger-config")
      .setOauth2RedirectUrl("此自定义接口暂时不支持 oauth2");
    var urls = apis.stream().map(api -> new OpenApiUrlsResponseResult()
      .setUrl("/v3/api-docs/" + api.getDisplayName())
      .setName(api.getDisplayName())).toList();
    result.setUrls(urls);
    log.debug("urls = {}", result);
    return mapper.writeValueAsString(result);
  }
}
