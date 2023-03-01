package io.tn.security.properties;

import io.tn.security.spring.security.BaseSecurityExceptionAdware;
import io.tn.security.spring.security.BaseSecurityPreFilter;
import io.tn.security.spring.security.BaseSecurityUserDetailsService;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

/**
 * spring security 安全策略配置
 *
 * @author TrueNine
 * @since 2022-12-10
 */
@Data
@Accessors(chain = true)
public class PolicyDesc {
  BaseSecurityPreFilter jwtPreFilter;
  BaseSecurityUserDetailsService service;
  BaseSecurityExceptionAdware exceptionAdware;
  List<String> anonymousPatterns = new ArrayList<>();
}
