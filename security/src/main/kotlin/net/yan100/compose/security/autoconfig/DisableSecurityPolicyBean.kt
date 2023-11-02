package net.yan100.compose.security.autoconfig;

import lombok.extern.slf4j.Slf4j;
import net.yan100.compose.security.annotations.EnableRestSecurity;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
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
    log.warn("生产环境请启用 WebSecurity, 使用 {} 来启用并配置完成 {}",
      EnableRestSecurity.class.getName(),
      SecurityPolicyBean.class.getName());
    return security
        .csrf(AbstractHttpConfigurer::disable)
        .authorizeHttpRequests(a -> a.anyRequest().permitAll())
        .logout(LogoutConfigurer::permitAll)
        .build();
  }

  @Bean
  UserDetailsService ssr() {
    log.warn("当前注册了一个临时的 InMemoryUserDetailsManager");
    return new InMemoryUserDetailsManager();
  }
}
