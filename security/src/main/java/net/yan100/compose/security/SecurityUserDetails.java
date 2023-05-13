package net.yan100.compose.security;

import lombok.extern.slf4j.Slf4j;
import net.yan100.compose.core.models.UserAuthorizationInfoModel;
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
public record SecurityUserDetails(UserAuthorizationInfoModel authModel) implements UserDetails {

  public SecurityUserDetails {
    log.trace("构建 = {}", authModel);
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    var roles = this.authModel.getRoles();
    var permissions = this.authModel.getPermissions();
    var auths = new ArrayList<GrantedAuthority>();
    roles.forEach(r -> auths.add(new SimpleGrantedAuthority("ROLE_" + r)));
    permissions.forEach(p -> auths.add(new SimpleGrantedAuthority(p)));
    return auths;
  }

  @Override
  public String getPassword() {
    return authModel.getEncryptedPassword();
  }

  @Override
  public String getUsername() {
    return authModel.getAccount();
  }

  @Override
  public boolean isAccountNonExpired() {
    return authModel.getNonExpired();
  }

  @Override
  public boolean isAccountNonLocked() {
    return authModel.getNonLocked();
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return authModel.getNonExpired();
  }

  @Override
  public boolean isEnabled() {
    return authModel.getEnabled();
  }
}
