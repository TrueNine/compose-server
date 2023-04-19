package com.truenine.component.security.models;


import com.truenine.component.security.spring.security.SecurityExceptionAdware;
import com.truenine.component.security.spring.security.SecurityPreflightValidFilter;
import com.truenine.component.security.spring.security.SecurityUserDetailsService;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * spring security 安全策略配置
 *
 * @author TrueNine
 * @since 2022-12-10
 */
@Data
public class SecurityPolicyDefineModel {
  SecurityPreflightValidFilter preValidFilter;
  SecurityUserDetailsService service;
  SecurityExceptionAdware exceptionAdware;
  List<String> anonymousPatterns = new ArrayList<>();
  List<String> swaggerPatterns = new ArrayList<>(List.of(
    "/v3/api-docs/**",
    "/v3/api-docs.yaml",
    "/doc.html**",
    "/swagger-ui/**"));
}
