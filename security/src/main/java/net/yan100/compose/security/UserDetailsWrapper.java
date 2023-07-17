package net.yan100.compose.security;

import lombok.extern.slf4j.Slf4j;
import net.yan100.compose.core.lang.Str;
import net.yan100.compose.core.models.AuthUserInfo;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;

/**
 * UserDetailsWrapper 包装类
 *
 * @author TrueNine
 * @since 2022-12-10
 */
@Slf4j
public record UserDetailsWrapper(AuthUserInfo authUserInfo) implements UserDetails {

  public UserDetailsWrapper {
    log.trace("构建 = {}", authUserInfo);
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    var auths = new ArrayList<GrantedAuthority>();
   // 添加角色信息
    this.authUserInfo.getRoles().forEach(r -> auths.add(new SimpleGrantedAuthority("ROLE_" + r)));
    // 添加权限信息
    this.authUserInfo.getPermissions().forEach(p -> auths.add(new SimpleGrantedAuthority(p)));
    // 添加 部门信息 到鉴权列表
    this.authUserInfo.getDepts().stream().filter(Str::hasText).map(r -> "DEPT_" + r).forEach(r -> auths.add(new SimpleGrantedAuthority(r)));

    return auths;
  }

  @Override
  public String getPassword() {
    return authUserInfo.getEncryptedPassword();
  }

  @Override
  public String getUsername() {
    return authUserInfo.getAccount();
  }

  @Override
  public boolean isAccountNonExpired() {
    return authUserInfo.getNonExpired();
  }

  @Override
  public boolean isAccountNonLocked() {
    return authUserInfo.getNonLocked();
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return authUserInfo.getNonExpired();
  }

  @Override
  public boolean isEnabled() {
    return authUserInfo.getEnabled();
  }
}
