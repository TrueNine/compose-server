package io.tn.security.autoconfig;

import io.tn.security.annotations.EnableRestSecurity;
import io.tn.security.properties.PolicyDesc;
import io.tn.security.spring.security.BaseSecurityExceptionAdware;
import io.tn.security.spring.security.BaseSecurityUserDetailsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@Configuration
@EnableWebSecurity
public class SecurityPolicyBean {

  private static EnableRestSecurity getAnno(ApplicationContext ctx) {
    var a = ctx.getBeansWithAnnotation(EnableRestSecurity.class);
    var s = new AtomicReference<EnableRestSecurity>();
    a.forEach((k, v) -> {
      s.set(v.getClass().getAnnotation(EnableRestSecurity.class));
      log.debug("采取的最后一个注解：{}，注解于：{}", s.get(), v.getClass().getName());
    });
    return s.get();
  }

  @Bean
  @Primary
  BaseSecurityUserDetailsService securityDetailsService(PolicyDesc desc) {
    return desc.getService();
  }

  @Bean
  @Primary
  BaseSecurityExceptionAdware securityExceptionAdware(PolicyDesc desc) {
    return desc.getExceptionAdware();
  }

  @Bean
  SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity,
                                          PolicyDesc desc,
                                          ApplicationContext ctx) throws Exception {
    var anonymous = desc.getAnonymousPatterns();
    var anno = getAnno(ctx);

    anonymous.addAll(List.of(anno.loginUrl()));
    anonymous.addAll(List.of(anno.logoutUrl()));

    if (anno.allowSwagger()) {
      anonymous.addAll(Arrays.stream(new String[]{
        "/v3/api-docs/**",
        "/v3/api-docs.yaml",
        "/doc.html**",
        "/swagger-ui/**"
      }).toList());
    }
    if (anno.allowWebJars()) {
      anonymous.addAll(Arrays.stream(new String[]{
        "/webjars/**",
        "/errors/**",
        "/error/**",
        "/favicon.ico"
      }).toList());
    }

    httpSecurity.addFilterBefore(desc.getJwtPreFilter(), UsernamePasswordAuthenticationFilter.class);
    httpSecurity
      .csrf().disable()
      .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
      .and()
      .authorizeHttpRequests()
      .requestMatchers(anonymous.toArray(String[]::new))
      .anonymous()
      .anyRequest().authenticated()
      .and()
      .userDetailsService(desc.getService());
    httpSecurity.exceptionHandling()
      .authenticationEntryPoint(desc.getExceptionAdware())
      .accessDeniedHandler(desc.getExceptionAdware());
    log.info("注册 Security 过滤器链 httpSecurity = {}", httpSecurity);
    return httpSecurity.build();
  }

  @Bean
  AuthenticationManager authenticationManager(AuthenticationConfiguration ac) throws Exception {
    log.info("注册 AuthenticationManager config = {}", ac);
    var manager = ac.getAuthenticationManager();
    log.info("获取到 AuthManager = {}", manager);
    return manager;
  }
}
