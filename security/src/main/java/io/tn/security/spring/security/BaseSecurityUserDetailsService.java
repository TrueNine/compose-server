package io.tn.security.spring.security;

import io.tn.security.spring.security.wrappers.AuthUserDetails;
import io.tn.security.spring.security.wrappers.Usr;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@Slf4j
public abstract class BaseSecurityUserDetailsService implements UserDetailsService {
  /**
   * 加载用户用户名
   *
   * @param username 用户名
   * @return {@link UserDetails}
   * @throws UsernameNotFoundException 用户名没有发现异常
   */
  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    log.info("加载 loadUserByUsername account = {}", username);
    return new AuthUserDetails(loadUserDetailsByAccount(username));
  }

  /**
   * 加载用户详细信息帐户
   *
   * @param account 账户
   * @return {@link Usr}
   */
  public abstract Usr loadUserDetailsByAccount(String account);
}
