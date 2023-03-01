package io.tn.security.spring.security.wrappers;

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
public record AuthUserDetails(Usr usr) implements UserDetails {

  public AuthUserDetails {
    log.info("构建 UserDetails = {}", usr);
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    var roles = this.usr.getRoles();
    var permissions = this.usr.getPermissions();
    var auths = new ArrayList<GrantedAuthority>();
    roles.forEach(r -> auths.add(new SimpleGrantedAuthority("ROLE_" + r)));
    permissions.forEach(p -> auths.add(new SimpleGrantedAuthority(p)));
    return auths;
  }

  @Override
  public String getPassword() {
    return usr.getPwd();
  }

  @Override
  public String getUsername() {
    return usr.getAccount();
  }

  @Override
  public boolean isAccountNonExpired() {
    return this.usr.getNonExpired();
  }

  @Override
  public boolean isAccountNonLocked() {
    return this.usr.getNonLocked();
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return this.usr.getNonExpired();
  }

  @Override
  public boolean isEnabled() {
    return this.usr.getEnabled();
  }
}
