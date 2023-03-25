package com.truenine.component.security.spring.security.wrappers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;

/**
 * SecurityUserDetails 包装类
 *
 * @author TrueNine
 * @since 2022-12-10
 */
@Slf4j
public record SecurityUserDetails(
  SecurityUserInfo securityUserInfo) implements UserDetails {

  public SecurityUserDetails {
    log.debug("构建 SecurityUserDetails = {}", securityUserInfo);
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    var roles = this.securityUserInfo.getRoles();
    var permissions = this.securityUserInfo.getPermissions();
    var auths = new ArrayList<GrantedAuthority>();
    roles.forEach(r -> auths.add(new SimpleGrantedAuthority("ROLE_" + r)));
    permissions.forEach(p -> auths.add(new SimpleGrantedAuthority(p)));
    return auths;
  }

  @Override
  public String getPassword() {
    return securityUserInfo.getPwd();
  }

  @Override
  public String getUsername() {
    return securityUserInfo.getAccount();
  }

  @Override
  public boolean isAccountNonExpired() {
    return this.securityUserInfo.getNonExpired();
  }

  @Override
  public boolean isAccountNonLocked() {
    return this.securityUserInfo.getNonLocked();
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return this.securityUserInfo.getNonExpired();
  }

  @Override
  public boolean isEnabled() {
    return this.securityUserInfo.getEnabled();
  }
}
