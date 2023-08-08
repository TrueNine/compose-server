package net.yan100.compose.webapidoc.properties;

import lombok.Data;
import net.yan100.compose.core.http.Headers;

@Data
public class JwtHeaderInfoProperties {
  private String authTokenName = Headers.AUTHORIZATION;
  private String refreshTokenName = Headers.X_REFRESH;
}
