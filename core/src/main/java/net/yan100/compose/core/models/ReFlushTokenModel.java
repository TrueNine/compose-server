package net.yan100.compose.core.models;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

/**
 * 过期时间令牌
 *
 * @author TrueNine
 * @since 2022-12-20
 */
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class ReFlushTokenModel {
  private String userId;
  private String deviceId;
  private String loginIpAddr;
  private LocalDateTime issueAt;
  private LocalDateTime expireTime;
}
