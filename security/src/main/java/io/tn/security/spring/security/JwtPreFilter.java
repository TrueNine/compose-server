package io.tn.security.spring.security;

import io.tn.security.jwt.JwtClient;
import io.tn.core.api.http.Headers;
import io.tn.security.jwt.exception.JwtTokenExpiredException;
import io.tn.security.jwt.exception.JwtTokenTamperedException;
import io.tn.security.jwt.exception.JwtUnknownException;
import io.tn.security.spring.security.wrappers.ReFlush;
import io.tn.security.spring.security.wrappers.Usr;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * 预置 jwt验证过滤器
 *
 * @author TrueNine
 * @since 2022-12-20
 */
public abstract class JwtPreFilter extends BaseSecurityPreFilter {

  public final JwtClient jwtClient;

  public JwtPreFilter(JwtClient client) {
    this.jwtClient = client;
  }

  @Override
  protected String getJwtFromRequest(HttpServletRequest request) {
    return request.getHeader(Headers.AUTHORIZATION);
  }

  @Override
  protected String getExpireFromRequest(HttpServletRequest request) {
    return request.getHeader(Headers.X_RE_FLUSH_TOKEN);
  }


  protected ReFlush converterExp(String exp, HttpServletRequest request)
      throws JwtTokenExpiredException,
      JwtTokenTamperedException,
      JwtUnknownException {
    return jwtClient.parseAndDecrypt(exp, ReFlush.class);
  }

  @Override
  protected Usr converterUsr(
      String jwt,
      String exp,
      final HttpServletRequest request,
      final HttpServletResponse response
  ) {
    Usr usr;
    boolean usrExpire = false;
    try {
      usr = jwtClient.parseAndDecrypt(jwt, Usr.class);
    } catch (JwtTokenExpiredException ex) {
      usr = ex.getSubject(Usr.class);
      usrExpire = true;
    } catch (Exception ex) {
      return null;
    }

    try {
      var expire = converterExp(exp, request);
      if (usrExpire) {
        response.setHeader(Headers.AUTHORIZATION, reissueUsrToken(usr));
        response.setHeader(Headers.X_RE_FLUSH_TOKEN, reissueExpToken(expire));
      }
    } catch (Exception ex) {
      return null;
    }
    return usr;
  }

  /**
   * 重新签发 用户令牌
   *
   * @param usr usr
   * @return usr jwt
   */
  protected abstract String reissueUsrToken(Usr usr);

  /**
   * 重新签发 过期令牌
   *
   * @param reFlush 过期令牌
   * @return exp jwt
   */
  protected abstract String reissueExpToken(ReFlush reFlush);
}
