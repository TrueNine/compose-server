package net.yan100.compose.security.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "compose.security.jwt")
public class JwtProperties {
    String publicKeyClassPath = "security/pub.key";
    String privateKeyClassPath = "security/pri.key";
    String encryptDataKeyName = "edt";
    String issuer = "T-SERVER";
    Long expiredDuration = (long) (2 * 60 * 60 * 60 * 1000);
}
