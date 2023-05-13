package net.yan100.compose.core.models;

import jakarta.annotation.Nullable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 基础用户传递信息
 *
 * @author T_teng
 * @since 2023-04-06
 */
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class BasicUserInfoModel {
  private @Nullable String userId;
  private @Nullable String account;
  private @Nullable String deviceId;
  private @Nullable String loginIpAddr;
  private @Nullable String currentIpAddr;
}
