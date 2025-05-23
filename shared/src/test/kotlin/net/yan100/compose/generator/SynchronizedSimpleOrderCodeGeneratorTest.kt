package net.yan100.compose.generator

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import net.yan100.compose.datetime
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.junit.jupiter.api.assertThrows
import org.springframework.boot.test.context.SpringBootTest
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@SpringBootTest
class SynchronizedSimpleOrderCodeGeneratorTest {
  private val snowflake =
    SynchronizedSimpleSnowflake(
      workId = 1,
      datacenterId = 1,
      sequence = 0,
      startTimeStamp = 0L,
    )

  private val generator = SynchronizedSimpleOrderCodeGenerator(snowflake)

  @Test
  fun `测试基本功能`() {
    val code = generator.nextString()
    assertAll(
      { assertTrue(code.length >= 19, "订单号长度应该至少为19位") },
      { assertTrue(code.matches(Regex("\\d+")), "订单号应该只包含数字") },
    )
  }

  @Test
  fun `测试订单号格式`() {
    val code = generator.nextString()
    val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")
    val currentDateTime = datetime.now()

    // 验证时间戳部分
    val timePart = code.substring(0, 17)
    val parsedDateTime = LocalDateTime.parse(timePart, dateTimeFormatter)

    assertAll(
      {
        assertTrue(
          parsedDateTime.isBefore(currentDateTime.plusSeconds(1)),
          "生成的时间应该不晚于当前时间",
        )
      },
      {
        assertTrue(
          parsedDateTime.isAfter(currentDateTime.minusSeconds(1)),
          "生成的时间应该不早于当前时间",
        )
      },
    )
  }

  @Test
  fun `测试唯一性`() {
    val codes = mutableSetOf<String>()
    repeat(1000) {
      val code = generator.nextString()
      assertTrue(codes.add(code), "生成的订单号应该是唯一的 $it $code")
    }
  }

  @Test
  fun `测试并发场景下的唯一性`() {
    val threadCount = 10
    val idsPerThread = 1000
    val totalIds = threadCount * idsPerThread
    val ids = ConcurrentHashMap.newKeySet<String>()
    val executor = Executors.newFixedThreadPool(threadCount)
    val latch = CountDownLatch(threadCount)

    repeat(threadCount) {
      executor.submit {
        try {
          repeat(idsPerThread) {
            val id = generator.nextString()
            synchronized(ids) {
              assertTrue(ids.add(id), "并发生成的订单号应该是唯一的，但发现重复订单号: $id")
            }
          }
        } finally {
          latch.countDown()
        }
      }
    }

    latch.await(5, TimeUnit.SECONDS)
    assertAll({ assertEquals(totalIds, ids.size, "应该生成 $totalIds 个唯一订单号") })
  }

  @Test
  fun `测试协程并发场景下的唯一性`() = runBlocking {
    val coroutineCount = 10
    val idsPerCoroutine = 1000
    val totalIds = coroutineCount * idsPerCoroutine
    val ids = ConcurrentHashMap.newKeySet<String>()

    val deferreds =
      (1..coroutineCount).map {
        async(Dispatchers.Default) {
          repeat(idsPerCoroutine) {
            val id = generator.nextString()
            synchronized(ids) {
              assertTrue(ids.add(id), "并发生成的订单号应该是唯一的，但发现重复订单号: $id")
            }
          }
        }
      }

    deferreds.awaitAll()
    assertEquals(totalIds, ids.size, "应该生成 $totalIds 个唯一订单号")
  }

  @Test
  fun `测试异常情况`() {
    val brokenSnowflake =
      object : ISnowflakeGenerator {
        override fun currentTimeMillis(): Long = System.currentTimeMillis()

        override fun next(): Long = throw RuntimeException("模拟异常")

        override fun nextString(): String = throw RuntimeException("模拟异常")
      }

    val brokenGenerator = SynchronizedSimpleOrderCodeGenerator(brokenSnowflake)

    assertThrows<RuntimeException>("应该抛出异常") { brokenGenerator.nextString() }
  }
}
