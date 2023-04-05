package com.truenine.component.security.models;


import com.truenine.component.security.spring.security.SecurityExceptionAdware;
import com.truenine.component.security.spring.security.SecurityPreValidFilter;
import com.truenine.component.security.spring.security.SecurityUserDetailsService;
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
public class SecurityPolicyDefineModel {
  SecurityPreValidFilter preValidFilter;
  SecurityUserDetailsService service;
  SecurityExceptionAdware exceptionAdware;
  List<String> anonymousPatterns = new ArrayList<>();
}
