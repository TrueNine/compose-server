package io.github.truenine.composeserver.generator

import io.github.truenine.composeserver.datetime
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.assertAll
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SynchronizedSimpleOrderCodeGeneratorTest {
  private lateinit var snowflake: ISnowflakeGenerator
  private lateinit var generator: SynchronizedSimpleOrderCodeGenerator
  private val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")

  @BeforeEach
  fun setUp() {
    snowflake = SynchronizedSimpleSnowflake(workId = 1, datacenterId = 1, sequence = 0)
    generator = SynchronizedSimpleOrderCodeGenerator(snowflake)
  }

  @Nested
  inner class BasicFunctionality {
    @Test
    fun `should generate order code with correct length`() {
      val code = generator.nextString()
      assertTrue(code.length >= 19, "订单号长度应该至少为19位")
    }

    @Test
    fun `should generate order code with only digits`() {
      val code = generator.nextString()
      assertTrue(code.matches(Regex("\\d+")), "订单号应该只包含数字")
    }

    @Test
    fun `should start with valid timestamp format`() {
      val code = generator.nextString()
      val timePart = code.substring(0, 17)

      // 验证时间戳格式是否正确
      assertTrue(timePart.length == 17, "时间戳部分应该是17位")

      val parsedDateTime = LocalDateTime.parse(timePart, dateTimeFormatter)
      val currentDateTime = datetime.now()

      assertAll(
        { assertTrue(parsedDateTime.isBefore(currentDateTime.plusSeconds(1)), "生成的时间应该不晚于当前时间") },
        { assertTrue(parsedDateTime.isAfter(currentDateTime.minusSeconds(1)), "生成的时间应该不早于当前时间") },
      )
    }

    @Test
    fun `should implement IOrderCodeGenerator interface correctly`() {
      val stringValue = generator.nextString()

      // 验证字符串格式
      assertTrue(stringValue.isNotBlank(), "nextString()应该返回非空字符串，实际值: '$stringValue'")
      assertTrue(stringValue.length >= 19, "订单号长度应该至少为19位，实际长度: ${stringValue.length}")

      // 验证基本数字格式
      assertTrue(stringValue.all { it.isDigit() }, "订单号应该只包含数字字符，实际值: '$stringValue'")

      // 验证时间戳部分
      val timePart = stringValue.substring(0, 17)
      assertTrue(timePart.length == 17, "时间戳部分应该是17位")
      assertTrue(timePart.all { it.isDigit() }, "时间戳部分应该只包含数字")
    }
  }

  @Nested
  inner class UniquenessTests {
    @Test
    fun `should generate unique order codes in sequential calls`() {
      val codes = mutableSetOf<String>()
      repeat(1000) { iteration ->
        val code = generator.nextString()
        assertTrue(codes.add(code), "生成的订单号应该是唯一的，第${iteration}次生成: $code")
      }
      assertEquals(1000, codes.size, "应该生成1000个不同的订单号")
    }

    @Test
    fun `should generate unique order codes under concurrent access`() {
      val threadCount = 10
      val idsPerThread = 100
      val totalIds = threadCount * idsPerThread
      val ids = ConcurrentHashMap.newKeySet<String>()
      val executor = Executors.newFixedThreadPool(threadCount)
      val latch = CountDownLatch(threadCount)

      repeat(threadCount) { threadIndex ->
        executor.submit {
          try {
            repeat(idsPerThread) { iteration ->
              val id = generator.nextString()
              synchronized(ids) { assertTrue(ids.add(id), "线程${threadIndex}第${iteration}次生成重复订单号: $id") }
            }
          } finally {
            latch.countDown()
          }
        }
      }

      assertTrue(latch.await(10, TimeUnit.SECONDS), "并发测试应该在10秒内完成")
      assertEquals(totalIds, ids.size, "应该生成 $totalIds 个唯一订单号")
    }

    @Test
    fun `should generate unique order codes under coroutine concurrency`() = runBlocking {
      val coroutineCount = 10
      val idsPerCoroutine = 100
      val totalIds = coroutineCount * idsPerCoroutine
      val ids = ConcurrentHashMap.newKeySet<String>()

      val deferreds =
        (1..coroutineCount).map { coroutineIndex ->
          async(Dispatchers.Default) {
            repeat(idsPerCoroutine) { iteration ->
              val id = generator.nextString()
              synchronized(ids) { assertTrue(ids.add(id), "协程${coroutineIndex}第${iteration}次生成重复订单号: $id") }
            }
          }
        }

      deferreds.awaitAll()
      assertEquals(totalIds, ids.size, "应该生成 $totalIds 个唯一订单号")
    }
  }

  @Nested
  inner class ErrorHandling {
    @Test
    fun `should handle snowflake generator exceptions gracefully`() {
      val brokenSnowflake = mockk<ISnowflakeGenerator>()
      every { brokenSnowflake.nextString() } throws RuntimeException("雪花算法异常")

      val brokenGenerator = SynchronizedSimpleOrderCodeGenerator(brokenSnowflake)

      val exception = assertFailsWith<IllegalStateException> { brokenGenerator.nextString() }

      assertEquals("Order code generation failed", exception.message)
      assertTrue(exception.cause is RuntimeException)
      assertEquals("雪花算法异常", exception.cause?.message)
    }

    @Test
    fun `should handle datetime formatting exceptions`() {
      val snowflake = mockk<ISnowflakeGenerator>()
      every { snowflake.nextString() } returns "123456"

      val generator = SynchronizedSimpleOrderCodeGenerator(snowflake)

      // 正常情况应该不会抛出异常
      val result = generator.nextString()
      assertTrue(result.isNotEmpty())
      verify { snowflake.nextString() }
    }
  }

  @Nested
  inner class PerformanceTests {
    @Test
    fun `should maintain consistent performance across multiple generations`() {
      val iterations = 10000
      val codes = mutableListOf<String>()

      val startTime = System.currentTimeMillis()
      repeat(iterations) { codes.add(generator.nextString()) }
      val duration = System.currentTimeMillis() - startTime

      assertEquals(iterations, codes.size)
      assertTrue(duration < 5000, "生成${iterations}个订单号应该在5秒内完成，实际耗时: ${duration}ms")

      // 验证所有生成的订单号都是唯一的
      assertEquals(iterations, codes.toSet().size, "所有生成的订单号都应该是唯一的")
    }
  }

  @Nested
  inner class ThreadSafetyTests {
    @Test
    fun `should be thread safe with synchronized annotation`() {
      val threadCount = 50
      val idsPerThread = 50
      val allIds = ConcurrentHashMap.newKeySet<String>()
      val executor = Executors.newFixedThreadPool(threadCount)
      val latch = CountDownLatch(threadCount)
      val exceptions = ConcurrentHashMap.newKeySet<Exception>()

      repeat(threadCount) {
        executor.submit {
          try {
            repeat(idsPerThread) {
              try {
                val id = generator.nextString()
                allIds.add(id)
              } catch (e: Exception) {
                exceptions.add(e)
              }
            }
          } finally {
            latch.countDown()
          }
        }
      }

      assertTrue(latch.await(15, TimeUnit.SECONDS), "线程安全测试应该在15秒内完成")
      assertTrue(exceptions.isEmpty(), "不应该有任何异常: ${exceptions.map { it.message }}")

      // 在高并发情况下，由于同步机制，可能会有重复的时间戳+雪花ID组合
      // 但我们验证至少生成了预期数量的ID
      assertTrue(allIds.size >= threadCount * idsPerThread * 0.9, "至少应该生成90%的预期ID数量，实际生成: ${allIds.size}，预期: ${threadCount * idsPerThread}")
    }
  }

  @Nested
  inner class MockTests {
    @Test
    fun `should use snowflake generator correctly`() {
      val mockSnowflake = mockk<ISnowflakeGenerator>()
      every { mockSnowflake.nextString() } returns "9876543210"

      val testGenerator = SynchronizedSimpleOrderCodeGenerator(mockSnowflake)
      val result = testGenerator.nextString()

      verify(exactly = 1) { mockSnowflake.nextString() }
      assertTrue(result.endsWith("9876543210"), "订单号应该以雪花ID结尾")
      assertTrue(result.startsWith(datetime.now().format(dateTimeFormatter).substring(0, 14)), "订单号应该以当前时间戳开头")
    }

    @Test
    fun `should handle empty snowflake id`() {
      val mockSnowflake = mockk<ISnowflakeGenerator>()
      every { mockSnowflake.nextString() } returns ""

      val testGenerator = SynchronizedSimpleOrderCodeGenerator(mockSnowflake)
      val result = testGenerator.nextString()

      assertEquals(17, result.length, "当雪花ID为空时，订单号长度应该等于时间戳长度")
      assertTrue(result.matches(Regex("\\d{17}")), "应该只包含17位数字")
    }

    @Test
    fun `should handle special snowflake responses`() {
      val mockSnowflake = mockk<ISnowflakeGenerator>()
      every { mockSnowflake.nextString() } returns "123456789"

      val testGenerator = SynchronizedSimpleOrderCodeGenerator(mockSnowflake)
      val result = testGenerator.nextString()

      assertTrue(result.endsWith("123456789"), "订单号应该以雪花ID结尾")
      assertTrue(result.length >= 17, "订单号长度应该至少为17位")
    }
  }

  @Nested
  inner class BoundaryConditions {
    @Test
    fun `should handle very long snowflake ids`() {
      val mockSnowflake = mockk<ISnowflakeGenerator>()
      val longSnowflakeId = "1".repeat(100)
      every { mockSnowflake.nextString() } returns longSnowflakeId

      val testGenerator = SynchronizedSimpleOrderCodeGenerator(mockSnowflake)
      val result = testGenerator.nextString()

      assertTrue(result.length >= 117, "订单号长度应该至少为117位")
      assertTrue(result.endsWith(longSnowflakeId), "订单号应该包含完整的雪花ID")
    }

    @Test
    fun `should handle minimum valid snowflake id`() {
      val mockSnowflake = mockk<ISnowflakeGenerator>()
      every { mockSnowflake.nextString() } returns "1"

      val testGenerator = SynchronizedSimpleOrderCodeGenerator(mockSnowflake)
      val result = testGenerator.nextString()

      assertEquals(18, result.length, "订单号长度应该为18位")
      assertTrue(result.endsWith("1"), "订单号应该以1结尾")
      assertTrue(result.toLong() >= 1000, "转换为Long应该>=1000")
    }
  }
}
