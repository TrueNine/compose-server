package io.tn.security.spring.security;

import io.tn.core.api.http.Headers;
import io.tn.core.api.http.ParameterNames;
import io.tn.core.lang.Str;
import io.tn.security.jwt.exception.*;
import io.tn.security.spring.security.wrappers.AuthUserDetails;
import io.tn.security.spring.security.wrappers.Usr;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.util.Objects;

/**
 * jwt过滤器
 *
 * @author TrueNine
 * @since 2022-10-28
 */
@Slf4j
public abstract class BaseSecurityPreFilter extends OncePerRequestFilter {

  @SneakyThrows
  @Override
  protected void doFilterInternal(final HttpServletRequest request,
                                  final HttpServletResponse response,
                                  final FilterChain filterChain) {
    var tenantRequestId = request.getParameter(ParameterNames.X_TENANT_ID);
    Usr usr;
    if (isJwtAuth(request)) {
      var jwt = getJwtFromRequest(request);
      var exp = getExpireFromRequest(request);
      usr = converterUsr(jwt, exp, request, response);
    } else {
      request.setAttribute(ParameterNames.X_TENANT_ID, tenantRequestId);
      filterChain.doFilter(request, response);
      return;
    }

    if (Objects.isNull(usr)) {
      filterChain.doFilter(request, response);
      return;
    }

    // 传出 tenant id
    request.setAttribute(Headers.X_INTERNAL_TENANT_ID, usr.getTenant());

    var details = new AuthUserDetails(usr);
    var upat = new UsernamePasswordAuthenticationToken(details, details.getPassword(), details.getAuthorities());
    SecurityContextHolder.getContext().setAuthentication(upat);
    filterChain.doFilter(request, response);
  }

  protected boolean isJwtAuth(HttpServletRequest request) {
    return Str.hasText(request.getHeader(Headers.AUTHORIZATION))
      && Str.hasText(request.getHeader(Headers.X_RE_FLUSH_TOKEN));
  }

  /**
   * 从请求得到jwt
   *
   * @param request 请求
   * @return {@link String}
   */
  protected abstract String getJwtFromRequest(HttpServletRequest request);

  /**
   * 从请求获得 exp 令牌
   *
   * @param request 请求
   * @return {@link String}
   */
  protected abstract String getExpireFromRequest(HttpServletRequest request);

  /**
   * jwt合法性检查
   *
   * @param jwt      jwt
   * @param exp      过期令牌
   * @param request  请求
   * @param response 响应
   * @return {@link Usr}
   * @throws JwtExpTokenExpiredException      exp令牌过期异常
   * @throws JwtTokenExpiredException         jwt令牌过期异常
   * @throws JwtTokenPairCrossDeviceException 令牌交叉设备异常
   * @throws JwtTokenTamperedException        令牌被篡改异常
   */
  protected abstract Usr converterUsr(String jwt,
                                      String exp,
                                      HttpServletRequest request,
                                      HttpServletResponse response)
    throws JwtExpTokenExpiredException,
    JwtTokenExpiredException,
    JwtTokenPairCrossDeviceException,
    JwtTokenTamperedException, JwtUnknownException;
}
