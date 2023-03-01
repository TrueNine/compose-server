package com.truenine.component.core.id;

import java.util.Date;

/**
 * 雪花
 * 雪花算法生成器
 *
 * @author TrueNine
 * @since 2022-10-28
 */
public interface Snowflake {
  /**
   * 下一个id
   *
   * @return 获取 一个 雪花算法生成的 id
   */
  long nextId();

  /**
   * 找到工作id
   *
   * @return 工作区id
   */
  long getWorkId();

  /**
   * 得到数据中心id
   *
   * @return 数据中心id
   */
  long getDatacenterId();

  /**
   * 开始时间,米尔斯
   *
   * @return 获得开始时间
   */
  long getStartTimeMillis();

  /**
   * 开始日期时间
   *
   * @return {@link Date}
   */
  default Date getStartDateTime() {
    return new Date(this.getStartTimeMillis());
  }

  /**
   * 获取时间戳
   *
   * @return timestamp 当前时间
   */
  default long getTimeStamp() {
    return System.currentTimeMillis();
  }

  /**
   * 下一个id str
   *
   * @return {@link String}
   */
  default String nextIdStr() {
    return "" + this.nextId();
  }
}
