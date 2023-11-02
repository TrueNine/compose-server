package net.yan100.compose.security.models;


import lombok.Data;
import net.yan100.compose.security.spring.security.SecurityExceptionAdware;
import net.yan100.compose.security.spring.security.SecurityPreflightValidFilter;
import net.yan100.compose.security.spring.security.SecurityUserDetailsService;

import java.util.ArrayList;
import java.util.List;

/**
 * spring security 安全策略配置
 *
 * @author TrueNine
 * @since 2022-12-10
 */
@Data
public class SecurityPolicyDefine {
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
