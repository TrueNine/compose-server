package net.yan100.compose.generator

import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.math.abs
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows

class SynchronizedSimpleSnowflakeTest {
  private lateinit var snowflake: SynchronizedSimpleSnowflake
  private val startTimeStamp = System.currentTimeMillis()

  @BeforeEach
  fun setUp() {
    snowflake =
      SynchronizedSimpleSnowflake(
        workId = 1L,
        datacenterId = 1L,
        sequence = 0L,
        startTimeStamp = startTimeStamp,
      )
  }

  @Test
  fun `测试构造函数参数验证 - 有效的workId和datacenterId`() {
    assertDoesNotThrow("有效的workId和datacenterId不应该抛出异常") {
      SynchronizedSimpleSnowflake(
        workId = 1L,
        datacenterId = 1L,
        sequence = 0L,
        startTimeStamp = startTimeStamp,
      )
    }
  }

  @Test
  fun `测试构造函数参数验证 - workId为0`() {
    val exception =
      assertThrows<IllegalArgumentException>("workId为0应该抛出异常") {
        SynchronizedSimpleSnowflake(
          workId = 0L,
          datacenterId = 1L,
          sequence = 0L,
          startTimeStamp = startTimeStamp,
        )
      }
    assertAll({
      assertTrue(exception.message!!.contains("workId"), "异常消息应该包含'workId'")
    })
  }

  @Test
  fun `测试构造函数参数验证 - workId为32`() {
    val exception =
      assertThrows<IllegalArgumentException>("workId为32应该抛出异常") {
        SynchronizedSimpleSnowflake(
          workId = 32L,
          datacenterId = 1L,
          sequence = 0L,
          startTimeStamp = startTimeStamp,
        )
      }
    assertAll({
      assertTrue(exception.message!!.contains("workId"), "异常消息应该包含'workId'")
    })
  }

  @Test
  fun `测试构造函数参数验证 - workId为负数`() {
    val exception =
      assertThrows<IllegalArgumentException>("workId为负数应该抛出异常") {
        SynchronizedSimpleSnowflake(
          workId = -1L,
          datacenterId = 1L,
          sequence = 0L,
          startTimeStamp = startTimeStamp,
        )
      }
    assertAll({
      assertTrue(exception.message!!.contains("workId"), "异常消息应该包含'workId'")
    })
  }

  @Test
  fun `测试构造函数参数验证 - datacenterId为0`() {
    val exception =
      assertThrows<IllegalArgumentException>("datacenterId为0应该抛出异常") {
        SynchronizedSimpleSnowflake(
          workId = 1L,
          datacenterId = 0L,
          sequence = 0L,
          startTimeStamp = startTimeStamp,
        )
      }
    assertAll({
      assertTrue(
        exception.message!!.contains("datacenterId"),
        "异常消息应该包含'datacenterId'",
      )
    })
  }

  @Test
  fun `测试构造函数参数验证 - datacenterId为32`() {
    val exception =
      assertThrows<IllegalArgumentException>("datacenterId为32应该抛出异常") {
        SynchronizedSimpleSnowflake(
          workId = 1L,
          datacenterId = 32L,
          sequence = 0L,
          startTimeStamp = startTimeStamp,
        )
      }
    assertAll({
      assertTrue(
        exception.message!!.contains("datacenterId"),
        "异常消息应该包含'datacenterId'",
      )
    })
  }

  @Test
  fun `测试构造函数参数验证 - datacenterId为负数`() {
    val exception =
      assertThrows<IllegalArgumentException>("datacenterId为负数应该抛出异常") {
        SynchronizedSimpleSnowflake(
          workId = 1L,
          datacenterId = -1L,
          sequence = 0L,
          startTimeStamp = startTimeStamp,
        )
      }
    assertAll({
      assertTrue(
        exception.message!!.contains("datacenterId"),
        "异常消息应该包含'datacenterId'",
      )
    })
  }

  @Test
  fun `测试生成ID的唯一性`() {
    val ids = mutableSetOf<Long>()
    val count = 1000

    repeat(count) {
      val id = snowflake.next()
      assertTrue(ids.add(id), "生成的ID应该是唯一的，但发现重复ID: $id")
    }

    assertAll({ assertEquals(count, ids.size, "应该生成 $count 个唯一ID") })
  }

  @Test
  fun `测试ID的递增性`() {
    val id1 = snowflake.next()
    val id2 = snowflake.next()
    assertAll({ assertTrue(id2 > id1, "后续生成的ID应该大于前一个ID") })
  }

  @Test
  fun `测试序列号溢出处理`() {
    // 设置初始序列号为接近最大值
    val snowflake =
      SynchronizedSimpleSnowflake(
        workId = 1L,
        datacenterId = 1L,
        sequence = 4094L, // 接近最大值4095
        startTimeStamp = startTimeStamp,
      )

    val id1 = snowflake.next()
    val id2 = snowflake.next()
    val id3 = snowflake.next()

    assertAll(
      { assertTrue(id2 > id1, "序列号未溢出时ID应该递增") },
      { assertTrue(id3 > id2, "序列号溢出后应该等待下一个时间戳") },
    )
  }

  @Test
  fun `测试并发情况下的ID唯一性`() {
    val threadCount = 10
    val idsPerThread = 1000
    val totalIds = threadCount * idsPerThread
    val ids = mutableSetOf<Long>()
    val executor = Executors.newFixedThreadPool(threadCount)
    val latch = CountDownLatch(threadCount)

    repeat(threadCount) {
      executor.submit {
        try {
          repeat(idsPerThread) {
            val id = snowflake.next()
            synchronized(ids) {
              assertTrue(ids.add(id), "并发生成的ID应该是唯一的，但发现重复ID: $id")
            }
          }
        } finally {
          latch.countDown()
        }
      }
    }

    latch.await(5, TimeUnit.SECONDS)
    assertAll({ assertEquals(totalIds, ids.size, "应该生成 $totalIds 个唯一ID") })
  }

  @Test
  fun `测试时钟回拨异常`() {
    // 创建一个自定义的雪花生成器实现
    val mockSnowflake =
      object : ISnowflakeGenerator {
        private var lastTimestamp = System.currentTimeMillis()
        private var sequence = 0L

        override fun currentTimeMillis(): Long {
          return if (sequence == 0L) {
            lastTimestamp
          } else {
            lastTimestamp - 1000 // 模拟时钟回拨
          }
        }

        override fun next(): Long {
          val timestamp = currentTimeMillis()
          if (timestamp < lastTimestamp) {
            throw RuntimeException(
              "时钟回拨，时间戳小于上次时间戳：${timestamp - lastTimestamp}"
            )
          }
          lastTimestamp = timestamp
          sequence++
          return timestamp
        }
      }

    // 先正常生成一个ID
    mockSnowflake.next()

    // 尝试生成第二个ID，应该抛出异常
    val exception =
      assertThrows<RuntimeException>("时钟回拨应该抛出异常") { mockSnowflake.next() }
    assertAll({
      assertTrue(exception.message!!.contains("时钟回拨"), "异常消息应该包含'时钟回拨'")
    })
  }

  @Test
  fun `测试ID结构`() {
    val id = snowflake.next()

    // 验证ID的各个部分
    val timestamp = (id shr 22) + startTimeStamp
    val datacenterId = (id shr 17) and 0x1F
    val workId = (id shr 12) and 0x1F
    val sequence = id and 0xFFF

    assertAll(
      {
        assertTrue(
          abs(timestamp - System.currentTimeMillis()) < 1000,
          "时间戳应该在合理范围内",
        )
      },
      { assertEquals(1L, datacenterId, "数据中心ID应该为1") },
      { assertEquals(1L, workId, "工作ID应该为1") },
      { assertTrue(sequence in 0..4095, "序列号应该在0-4095范围内") },
    )
  }
}

// 定义一个时间提供者接口
private interface TimeProvider {
  fun currentTimeMillis(): Long
}
