package net.yan100.compose.core.generator

class SynchronizedSimpleSnowflake(
  private val workId: Long,
  private val datacenterId: Long,
  private var sequence: Long,
  private val startTimeStamp: Long,
) : ISnowflakeGenerator {
  companion object {
    // 位长度定义
    private const val WORKER_ID_BITS = 5L
    private const val DATACENTER_ID_BITS = 5L
    private const val SEQUENCE_BITS = 12L

    // 最大值计算
    private val MAX_WORKER_ID = (-1L shl WORKER_ID_BITS.toInt()).inv()
    private val MAX_DATACENTER_ID = (-1L shl DATACENTER_ID_BITS.toInt()).inv()
    private val SEQUENCE_MASK = (-1L shl SEQUENCE_BITS.toInt()).inv()

    // 位移量计算
    private val WORKER_ID_SHIFT = SEQUENCE_BITS
    private val DATACENTER_ID_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS
    private val TIMESTAMP_LEFT_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS + DATACENTER_ID_BITS
  }

  // 上次时间戳，初始值为负数
  private var lastTimestamp = -1L

  init {
    require(workId in 1 .. MAX_WORKER_ID) {
      "workId 必须在 1 到 $MAX_WORKER_ID 之间"
    }
    require(datacenterId in 1 .. MAX_DATACENTER_ID) {
      "datacenterId 必须在 1 到 $MAX_DATACENTER_ID 之间"
    }
    lastTimestamp = currentTimeMillis()
  }

  override fun currentTimeMillis(): Long {
    return System.currentTimeMillis()
  }

  @Synchronized
  override fun next(): Long {
    var timestamp = currentTimeMillis()

    // 处理时钟回拨
    if (timestamp < lastTimestamp) {
      throw RuntimeException("时钟回拨，时间戳小于上次时间戳：${timestamp - lastTimestamp}")
    }

    // 同一毫秒内生成多个ID
    if (timestamp == lastTimestamp) {
      sequence = (sequence + 1) and SEQUENCE_MASK
      if (sequence == 0L) {
        timestamp = tilNextTimeMillis(lastTimestamp)
      }
    } else {
      sequence = 0
    }

    lastTimestamp = timestamp

    // 组合各部分生成最终ID
    return ((timestamp - startTimeStamp) shl TIMESTAMP_LEFT_SHIFT.toInt()) or
      (datacenterId shl DATACENTER_ID_SHIFT.toInt()) or
      (workId shl WORKER_ID_SHIFT.toInt()) or
      sequence
  }

  private fun tilNextTimeMillis(lastTimestamp: Long): Long {
    var timestamp = currentTimeMillis()
    while (timestamp <= lastTimestamp) {
      timestamp = currentTimeMillis()
    }
    return timestamp
  }
}
