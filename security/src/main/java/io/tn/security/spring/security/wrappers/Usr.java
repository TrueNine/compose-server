package io.tn.security.spring.security.wrappers;

import jakarta.annotation.Nullable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * security校验所需的用户身份
 *
 * @author TrueNine
 * @since 2022-12-10
 */
@Getter
@Setter
@ToString
@EqualsAndHashCode
@Accessors(chain = true)
public final class Usr {
  private @Nullable String id;
  private @Nullable String account;
  private @Nullable String pwd;
  private @Nullable String tenant;
  private String deviceId;
  private String loginIp;
  private Boolean nonLocked = true;
  private Boolean nonExpired = false;
  private Boolean enabled = false;
  private List<String> roles = new CopyOnWriteArrayList<>();
  private List<String> permissions = new CopyOnWriteArrayList<>();
}
