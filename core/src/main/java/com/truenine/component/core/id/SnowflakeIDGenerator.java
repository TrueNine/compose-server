package com.truenine.component.core.id;

import lombok.extern.slf4j.Slf4j;

/**
 * id generator
 * 雪花算法的静态调用方式
 *
 * @author TrueNine
 * @since 2022-10-28
 */
@Slf4j
public final class SnowflakeIDGenerator {
  private static Snowflake snowflake;

  public static void setSnowflake(Snowflake idGen, boolean isReset) {
    if (null != snowflake && !isReset) {
      throw new IllegalArgumentException("不能重复设置静态参数的值，已经被初始化，可以设置 reset 的值为 true，但不建议。");
    }
    log.debug("snowflake 的实例已被重置 = {}", idGen);
    snowflake = idGen;
  }

  public static long nextId() {
    return snowflake.nextId();
  }

  public static String nextIdStr() {
    return snowflake.nextIdStr();
  }
}
