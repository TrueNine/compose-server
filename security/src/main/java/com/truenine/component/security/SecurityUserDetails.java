package com.truenine.component.security;

import com.truenine.component.core.models.UserAuthorizationInfoModel;
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
  UserAuthorizationInfoModel userAuthorizationInfoModel) implements UserDetails {

  public SecurityUserDetails {
    log.debug("构建 SecurityUserDetails = {}", userAuthorizationInfoModel);
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    var roles = this.userAuthorizationInfoModel.getRoles();
    var permissions = this.userAuthorizationInfoModel.getPermissions();
    var auths = new ArrayList<GrantedAuthority>();
    roles.forEach(r -> auths.add(new SimpleGrantedAuthority("ROLE_" + r)));
    permissions.forEach(p -> auths.add(new SimpleGrantedAuthority(p)));
    return auths;
  }

  @Override
  public String getPassword() {
    return userAuthorizationInfoModel.getPwd();
  }

  @Override
  public String getUsername() {
    return userAuthorizationInfoModel.getAccount();
  }

  @Override
  public boolean isAccountNonExpired() {
    return this.userAuthorizationInfoModel.getNonExpired();
  }

  @Override
  public boolean isAccountNonLocked() {
    return this.userAuthorizationInfoModel.getNonLocked();
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return this.userAuthorizationInfoModel.getNonExpired();
  }

  @Override
  public boolean isEnabled() {
    return this.userAuthorizationInfoModel.getEnabled();
  }
}
