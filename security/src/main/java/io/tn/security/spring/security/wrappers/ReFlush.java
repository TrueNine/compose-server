package io.tn.security.spring.security.wrappers;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

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
@Accessors(chain = true)
public class ReFlush {
  private String userId;
  private String deviceId;
  private LocalDateTime issueAt;
  private LocalDateTime expireTime;
}
