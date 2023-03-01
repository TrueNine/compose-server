package com.truenine.component.security.autoconfig;

import com.truenine.component.security.annotations.EnableRestSecurity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

/**
 * spring security 策略注册器
 *
 * @author TrueNine
 * @since 2022-12-14
 */
@ConditionalOnMissingBean({
  SecurityPolicyBean.class
})
@Slf4j
public class DisableSecurityPolicyBean {

  @Bean
  SecurityFilterChain disableSecurityFilterChain(HttpSecurity security) throws Exception {
    log.warn("警告：生产环境请启用 webSecurity, 使用 {} 来启用并配置 {}",
      EnableRestSecurity.class.getName(),
      SecurityPolicyBean.class.getName());
    return
      security
        .csrf()
        .disable()
        .authorizeHttpRequests()
        .anyRequest()
        .permitAll()
        .and()
        .logout()
        .permitAll()
        .and().build();
  }

  @Bean
  UserDetailsService ssr() {
    return new InMemoryUserDetailsManager();
  }
}
