package com.truenine.component.core.id;

import java.util.UUID;

/**
 * uuid
 *
 * @author TrueNine
 * @since 2022-10-29
 */
public class UUIDGenerator {
  /**
   * str
   *
   * @return {@link String}
   */
  public static String str() {
    return UUID.randomUUID().toString();
  }
}
