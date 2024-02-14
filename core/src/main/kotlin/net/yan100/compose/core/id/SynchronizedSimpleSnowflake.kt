package net.yan100.compose.core.id

class SynchronizedSimpleSnowflake(
    private val workId: Long,
    private val datacenterId: Long,
    private var sequence: Long,
    private val startTimeStamp: Long
) : Snowflake {
    // 长度为5位
    private val workerIdBits = 5L
    private val datacenterIdBits = 5L

    // 最大值
    private val maxWorkerId = (-1L shl workerIdBits.toInt()).inv()
    private val maxDatacenterId = (-1L shl datacenterIdBits.toInt()).inv()

    // 上次时间戳，初始值为负数
    private var lastTimestamp = -1L

    init {
        require(!(workId <= 0 || workId >= maxWorkerId)) { "workId 大于$maxDatacenterId 或者小于0" }
        require(!(datacenterId <= 0 || datacenterId >= maxDatacenterId)) { "datacenterId 大于 $maxDatacenterId 或者小于0" }
        lastTimestamp = currentTimeMillis()
    }

    override fun currentTimeMillis(): Long {
        return System.currentTimeMillis()
    }

    // 序列号id长度
    private val sequenceBits = 12L

    // 序列号最大值
    private val sequenceMask = (-1L shl sequenceBits.toInt()).inv()

    // 工作id需要左移的位数，12位
    private val workerIdShift = sequenceBits

    // 数据id需要左移位数 12+5=17位
    private val datacenterIdShift = sequenceBits + workerIdBits

    // 时间戳需要左移位数 12+5+5=22位
    private val timestampLeftShift = sequenceBits + workerIdBits + datacenterIdBits

    @Synchronized
    override fun nextId(): Long {
        var timeStamp: Long = currentTimeMillis()
        if (lastTimestamp > timeStamp) {
            throw RuntimeException("时间小于当前时间戳：" + (timeStamp - lastTimestamp))
        }
        if (lastTimestamp == timeStamp) {
            sequence = sequence + 1 and sequenceMask
            if (0L == sequence) {
                timeStamp = tilNextTimeMillis(lastTimestamp)
            }
        } else {
            sequence = 0
        }
        lastTimestamp = timeStamp
        return timeStamp - startTimeStamp shl timestampLeftShift.toInt() or
            (datacenterId shl datacenterIdShift.toInt()) or
            (workId shl workerIdShift.toInt()) or
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
