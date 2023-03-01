package com.truenine.component.security.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.truenine.component.core.encrypt.Enc;
import com.truenine.component.core.encrypt.consts.RsaKeyPair;
import com.truenine.component.security.jwt.exception.JwtTokenExpiredException;
import com.truenine.component.security.jwt.exception.JwtTokenTamperedException;
import com.truenine.component.security.jwt.exception.JwtUnknownException;

import java.security.interfaces.RSAPublicKey;

/**
 * jwt客户端
 *
 * @author TrueNine
 * @since 2022-10-28
 */
public class JwtClient {
  protected ObjectMapper mapper;
  protected RsaKeyPair pair;
  protected String issuer;

  protected JwtClient() {
    this(null, null);
  }

  private JwtClient(RsaKeyPair pair, String issuer) {
    this.pair = pair;
    this.issuer = issuer;
  }

  public static JwtClient creator(RsaKeyPair pair,
                                  String issuer) {
    return new JwtClient(pair, issuer);
  }

  public JwtClient setMapper(ObjectMapper mapper) {
    this.mapper = mapper;
    return this;
  }

  private JwtTokenExpiredException expireThrowHandle(DecodedJWT preVerify) {
    if (null != preVerify) {
      return new JwtTokenExpiredException(preVerify.getSubject(), mapper);
    } else {
      return new JwtTokenExpiredException("{}", mapper);
    }
  }

  public <T> T parseAndDecrypt(String token, Class<T> type)
    throws JwtTokenTamperedException,
    JwtUnknownException,
    JwtTokenExpiredException {
    return parseAndDecrypt(token, type,
      this.pair.getPub(),
      this.issuer
    );
  }

  public <T> T parseAndDecrypt(String token,
                               Class<T> type,
                               RSAPublicKey key,
                               String issuer)
    throws
    JwtTokenTamperedException,
    JwtUnknownException,
    JwtTokenExpiredException {
    var jti = parsePre(token, key, issuer);
    var subject = jti.getSubject();
    return wrapperJwtToken(jti, type, subject);
  }

  private DecodedJWT parsePre(String token,
                              RSAPublicKey key,
                              String issuer)
    throws JwtTokenExpiredException, JwtTokenTamperedException, JwtUnknownException {

    // 进行 rsa 强校验，并转接异常处理
    var verify = JWT
      .require(Algorithm.RSA256(key))
      .withIssuer(issuer)
      .acceptLeeway(0)
      .build();
    return auth0parseExceptionHandler(token, verify);
  }

  private <T> T wrapperJwtToken(DecodedJWT v,
                                Class<T> type,
                                String decPayload) {
    try {
      return mapper.readValue(
        null == decPayload ?
          v.getSubject()
          : Enc.decRsaBy(pair.getPub(), decPayload, Enc.getCharset()),
        type);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }

  private DecodedJWT auth0parseExceptionHandler(String token, JWTVerifier verify)
    throws
    JwtUnknownException,
    JwtTokenExpiredException,
    JwtTokenTamperedException {
    DecodedJWT preVerify = null;
    try {
      // 进行预验证处理
      preVerify = JWT.decode(token);
      return verify.verify(token);
    } catch (TokenExpiredException e /* token过期 */) {
      throw expireThrowHandle(preVerify);
    } catch (SignatureVerificationException e /* 签名验证错误 */) {
      throw new JwtTokenTamperedException();
    } catch (Exception ex /* 兜底异常 */) {
      ex.printStackTrace();
      throw new JwtUnknownException();
    }
  }

  public <T> T parse(String token,
                     Class<T> type) throws JwtTokenTamperedException, JwtUnknownException, JwtTokenExpiredException {
    return parse(token, type, this.pair.getPub(), issuer);
  }

  public <T> T parse(String token,
                     Class<T> type,
                     RSAPublicKey key,
                     String issuer)
    throws
    JwtTokenTamperedException,
    JwtUnknownException,
    JwtTokenExpiredException {

    var jti = parsePre(token, key, issuer);
    return wrapperJwtToken(jti, type, jti.getSubject());
  }
}
