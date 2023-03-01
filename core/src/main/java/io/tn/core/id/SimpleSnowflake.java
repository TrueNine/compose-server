package io.tn.core.id;

/**
 * 简单雪花
 *
 * @author TrueNine
 * @since 2022-10-28
 */
public class SimpleSnowflake implements Snowflake {
  /**
   * 工作id
   */
  private final long workId;

  /**
   * 数据中心id
   */
  private final long datacenterId;
  /**
   * 开始时间戳
   */
  private final long startTimeStamp;

  /**
   * 工人id比特
   */
  private final long workerIdBits = 5L;
  private final long datacenterIdBits = 5L;

  /**
   * 马克斯工人id
   */
  private final long maxWorkerId = ~(-1L << workerIdBits);
  /**
   * 最大数据中心id
   */
  private final long maxDatacenterId = ~(-1L << datacenterIdBits);
  /**
   * 位序列
   */
  private final long sequenceBits = 12L;
  /**
   * 序列号最大值
   */
  private final long sequenceMask = ~(-1L << sequenceBits);
  /**
   * 工作id需要左移的位数，12位
   */
  private final long workerIdShift = sequenceBits;
  /**
   * 数据id需要左移位数 12+5=17位
   */
  private final long datacenterIdShift = sequenceBits + workerIdBits;
  /**
   * 时间戳需要左移位数 12+5+5=22位
   */
  private final long timestampLeftShift = sequenceBits + workerIdBits + datacenterIdBits;
  private long sequence;
  /**
   * 上次时间戳，初始值为负数
   */
  private long lastTimestamp = -1L;

  public SimpleSnowflake(long workId, long datacenterId, long sequence, long startTimeStamp) {
    if (workId <= 0 || workId >= maxWorkerId) {
      throw new IllegalArgumentException("workId tooSmall Or TooBig");
    }
    if (datacenterId <= 0 || datacenterId >= this.maxDatacenterId) {
      throw new IllegalArgumentException("datacenterIdBits tooSmall Or TooBig");
    }

    this.workId = workId;
    this.datacenterId = datacenterId;
    this.sequence = sequence;
    this.startTimeStamp = startTimeStamp;
    this.lastTimestamp = this.getTimeStamp();
  }

  @Override
  public synchronized long nextId() {
    long timeStamp = this.getTimeStamp();
    if (lastTimestamp > timeStamp) {
      throw new RuntimeException("时间小于当前时间戳：" + (timeStamp - lastTimestamp));
    }
    if (lastTimestamp == timeStamp) {
      this.sequence = (sequence + 1) & sequenceMask;
      if (0 == sequence) {
        timeStamp = this.tilNextTimeMillis(lastTimestamp);
      }
    } else {
      sequence = 0;
    }
    this.lastTimestamp = timeStamp;


    return ((timeStamp - startTimeStamp) << timestampLeftShift) |
      (datacenterId << datacenterIdShift) |
      (this.workId << workerIdShift) |
      sequence;
  }

  @Override
  public long getWorkId() {
    return this.workId;
  }

  @Override
  public long getDatacenterId() {
    return this.datacenterId;
  }

  @Override
  public long getStartTimeMillis() {
    return startTimeStamp;
  }

  private long tilNextTimeMillis(long lastTimestamp) {
    long timestamp = this.getTimeStamp();
    while (timestamp <= lastTimestamp) {
      timestamp = this.getTimeStamp();
    }
    return timestamp;
  }
}
