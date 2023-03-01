package io.tn.security.spring.security;


import io.tn.core.api.http.R;
import io.tn.core.api.http.Status;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;

import java.io.IOException;


/**
 * 异常过滤器
 *
 * @author TrueNine
 * @since 2022-09-28
 */
@Slf4j
public abstract class BaseSecurityExceptionAdware
  implements
  AccessDeniedHandler,
  AuthenticationEntryPoint {

  @Override
  public void commence(
    final HttpServletRequest request,
    final HttpServletResponse response,
    AuthenticationException ex)
    throws IOException {
    log.info("授权异常");
    R.failed(ex, Status._401).writeJson(response);
  }

  @Override
  public void handle(
    final HttpServletRequest request,
    final HttpServletResponse response,
    AccessDeniedException ex) throws
    IOException {
    log.info("无权限异常");
    R.failed(ex, 403).writeJson(response);
  }
}
