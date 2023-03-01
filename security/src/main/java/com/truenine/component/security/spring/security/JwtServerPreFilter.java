package com.truenine.component.security.spring.security;

import com.truenine.component.security.jwt.JwtClient;
import com.truenine.component.security.jwt.JwtServer;
import com.truenine.component.security.spring.security.wrappers.ReFlush;
import com.truenine.component.security.spring.security.wrappers.Usr;

import java.time.LocalDateTime;

public class JwtServerPreFilter extends JwtPreFilter {

  private JwtServer jwtServer;

  public JwtServerPreFilter(JwtClient client, JwtServer jwtServer) {
    this(client);
    this.jwtServer = jwtServer;
  }

  public JwtServerPreFilter(JwtClient client) {
    super(client);
  }

  @Override
  protected String reissueUsrToken(Usr usr) {
    return jwtServer.createAndEncryptSubject(usr);
  }

  @Override
  protected String reissueExpToken(ReFlush reFlush) {
    reFlush.setIssueAt(LocalDateTime.now());
    return jwtServer.createAndEncryptSubject(reFlush);
  }
}
