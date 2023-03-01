package io.tn.security.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.tn.core.encrypt.Enc;
import io.tn.core.encrypt.consts.RsaKeyPair;
import io.tn.core.lang.DTimer;

import java.security.interfaces.RSAPrivateKey;
import java.time.Duration;
import java.util.Date;
import java.util.UUID;


/**
 * jwt服务器
 *
 * @author TrueNine
 * @since 2022-12-09
 */
public class JwtServer extends JwtClient {
  protected Long expireMillis;

  private JwtServer(RsaKeyPair pair,
                    Long expireMillis,
                    String issuer) {
    this.issuer = issuer;
    this.pair = pair;
    this.expireMillis = expireMillis;
  }

  public static JwtServer creator(RsaKeyPair pair,
                                  Long expireMillis,
                                  String issuer) {
    return new JwtServer(pair, expireMillis, issuer);
  }

  @Override
  public JwtServer setMapper(ObjectMapper mapper) {
    this.mapper = mapper;
    return this;
  }

  public String createAndEncryptSubject(Object subject) {
    return createAndEncryptSubject(subject,
      Duration.ofMillis(this.expireMillis));
  }

  public String createAndEncryptSubject(Object subject, Duration expire) {
    try {
      return createAndEncryptSubject(this.pair.getPri(),
        this.pair.getPri(), UUID.randomUUID().toString(), this.issuer,
        mapper.writeValueAsString(subject), expire.toMillis());
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }

  public String createAndEncryptSubject(RSAPrivateKey key,
                                        RSAPrivateKey subKey,
                                        String id,
                                        String issuer,
                                        String subject,
                                        long expires) {

    var encryptedSubject = Enc.encRsaBy(subKey, subject, Enc.SHARDING_SIZE, Enc.getCharset());
    return create(
      key, id, issuer,
      encryptedSubject, expires
    );
  }

  public String create(RSAPrivateKey key,
                       String id,
                       String issuer,
                       String subject,
                       long expires) {
    var creator = JWT.create()
      .withJWTId(id)
      .withIssuer(issuer)
      .withIssuedAt(new Date())
      .withSubject(subject)
      .withExpiresAt(DTimer.plusDate(expires));
    return creator.sign(Algorithm.RSA256(key));
  }

  public String create(Object subject) {
    try {
      return create(
        this.pair.getPri(),
        UUID.randomUUID().toString(),
        this.issuer,
        mapper.writeValueAsString(subject),
        this.expireMillis
      );
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }

  public String createAndEncryptSubject(RSAPrivateKey subKey, Object subject) {
    try {
      return createAndEncryptSubject(subKey,
        this.pair.getPri(), UUID.randomUUID().toString(), this.issuer,
        mapper.writeValueAsString(subject), this.expireMillis);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }
}
