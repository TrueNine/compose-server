package net.yan100.compose.core.models;

import jakarta.annotation.Nullable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

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
@EqualsAndHashCode(callSuper = true)
public final class UserAuthorizationInfoModel extends BasicUserInfoModel {
  private @Nullable String encryptedPassword;
  private Boolean nonLocked = true;
  private Boolean nonExpired = false;
  private Boolean enabled = false;
  private List<String> roles = new CopyOnWriteArrayList<>();
  private List<String> permissions = new CopyOnWriteArrayList<>();
}
