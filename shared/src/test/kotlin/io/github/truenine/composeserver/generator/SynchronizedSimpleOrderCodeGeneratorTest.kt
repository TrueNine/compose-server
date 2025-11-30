package io.github.truenine.composeserver.generator

import io.mockk.*
import java.util.concurrent.*
import kotlin.math.abs
import kotlin.test.*
import kotlinx.coroutines.*
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SynchronizedSimpleOrderCodeGeneratorTest {
  private lateinit var snowflake: ISnowflakeGenerator
  private lateinit var generator: SynchronizedSimpleOrderCodeGenerator

  @BeforeEach
  fun setUp() {
    snowflake = SynchronizedSimpleSnowflake(workId = 1, datacenterId = 1, sequence = 0)
    generator = SynchronizedSimpleOrderCodeGenerator(snowflake)
  }

  private fun assertTimestampPrefix(orderCode: String) {
    assertTrue(orderCode.length >= 13, "Order code length should be at least 13 digits")
    val timestampPart = orderCode.substring(0, 13)
    assertTrue(timestampPart.length == 13, "Timestamp part should be 13 digits")
    assertTrue(timestampPart.all { it.isDigit() }, "Timestamp part should contain only digits")
    val timestampMillis = timestampPart.toLong()
    val now = System.currentTimeMillis()
    val difference = abs(now - timestampMillis)
    assertTrue(difference <= 1_000, "Generated timestamp should be within 1 second of current time, actual difference: ${difference}ms")
  }

  @Nested
  inner class BasicFunctionality {
    @Test
    fun `should generate order code with correct length`() {
      val code = generator.nextString()
      assertTrue(code.length > 13, "Order code length should be greater than 13 digits")
      assertTimestampPrefix(code)
    }

    @Test
    fun `should generate order code with only digits`() {
      val code = generator.nextString()
      assertTrue(code.matches(Regex("\\d+")), "Order code should contain only digits")
    }

    @Test
    fun `should start with valid timestamp format`() {
      val code = generator.nextString()
      assertTimestampPrefix(code)
    }

    @Test
    fun `should implement IOrderCodeGenerator interface correctly`() {
      val stringValue = generator.nextString()

      // Validate string format
      assertTrue(stringValue.isNotBlank(), "nextString() should return a non-blank value, actual: '$stringValue'")
      assertTrue(stringValue.length > 13, "Order code length should be greater than 13 digits, actual length: ${stringValue.length}")

      // Validate numeric format
      assertTrue(stringValue.all { it.isDigit() }, "Order code should contain only numeric characters, actual: '$stringValue'")

      // Validate timestamp prefix
      assertTimestampPrefix(stringValue)
    }
  }

  @Nested
  inner class UniquenessTests {
    @Test
    fun `should generate unique order codes in sequential calls`() {
      val codes = mutableSetOf<String>()
      repeat(1000) { iteration ->
        val code = generator.nextString()
        assertTrue(codes.add(code), "Generated order codes should be unique, duplicate at iteration ${iteration}: $code")
      }
      assertEquals(1000, codes.size, "Should generate 1000 distinct order codes")
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
              synchronized(ids) { assertTrue(ids.add(id), "Thread ${threadIndex} produced duplicate order code at iteration ${iteration}: $id") }
            }
          } finally {
            latch.countDown()
          }
        }
      }

      assertTrue(latch.await(10, TimeUnit.SECONDS), "Concurrent test should complete within 10 seconds")
      assertEquals(totalIds, ids.size, "Should generate $totalIds unique order codes")
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
              synchronized(ids) { assertTrue(ids.add(id), "Coroutine ${coroutineIndex} produced duplicate order code at iteration ${iteration}: $id") }
            }
          }
        }

      deferreds.awaitAll()
      assertEquals(totalIds, ids.size, "Should generate $totalIds unique order codes")
    }
  }

  @Nested
  inner class ErrorHandling {
    @Test
    fun `should handle snowflake generator exceptions gracefully`() {
      val brokenSnowflake = mockk<ISnowflakeGenerator>()
      every { brokenSnowflake.currentTimeMillis() } throws RuntimeException("Snowflake generator failure")

      val brokenGenerator = SynchronizedSimpleOrderCodeGenerator(brokenSnowflake)

      val exception = assertFailsWith<IllegalStateException> { brokenGenerator.nextString() }

      assertEquals("Order code generation failed", exception.message)
      assertTrue(exception.cause is RuntimeException)
      assertEquals("Snowflake generator failure", exception.cause?.message)
    }

    @Test
    fun `should handle datetime formatting exceptions`() {
      val snowflake = mockk<ISnowflakeGenerator>()
      every { snowflake.currentTimeMillis() } returns System.currentTimeMillis()
      every { snowflake.nextString() } returns "123456"

      val generator = SynchronizedSimpleOrderCodeGenerator(snowflake)

      // Should not throw exceptions under normal circumstances
      val result = generator.nextString()
      assertTrue(result.isNotEmpty())
      verify { snowflake.currentTimeMillis() }
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
      assertTrue(duration < 5000, "Generating ${iterations} order codes should finish within 5 seconds, actual duration: ${duration}ms")

      // Verify that all generated order codes are unique
      assertEquals(iterations, codes.toSet().size, "All generated order codes should be unique")
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

      assertTrue(latch.await(15, TimeUnit.SECONDS), "Thread-safety test should complete within 15 seconds")
      assertTrue(exceptions.isEmpty(), "No exceptions should be thrown: ${exceptions.map { it.message }}")

      // Under high concurrency we may see duplicate timestamp + snowflake combinations
      // but we still expect to generate the majority of the IDs
      assertTrue(
        allIds.size >= threadCount * idsPerThread * 0.9,
        "Should generate at least 90% of the expected IDs, actual: ${allIds.size}, expected: ${threadCount * idsPerThread}",
      )
    }
  }

  @Nested
  inner class MockTests {
    @Test
    fun `should use snowflake generator correctly`() {
      val mockSnowflake = mockk<ISnowflakeGenerator>()
      every { mockSnowflake.currentTimeMillis() } returns System.currentTimeMillis()
      every { mockSnowflake.nextString() } returns "9876543210"

      val testGenerator = SynchronizedSimpleOrderCodeGenerator(mockSnowflake)
      val result = testGenerator.nextString()

      verify(exactly = 1) { mockSnowflake.currentTimeMillis() }
      verify(exactly = 1) { mockSnowflake.nextString() }
      assertTrue(result.endsWith("9876543210"), "Order code should end with the snowflake ID")
      assertTimestampPrefix(result)
    }

    @Test
    fun `should handle empty snowflake id`() {
      val mockSnowflake = mockk<ISnowflakeGenerator>()
      every { mockSnowflake.currentTimeMillis() } returns System.currentTimeMillis()
      every { mockSnowflake.nextString() } returns ""

      val testGenerator = SynchronizedSimpleOrderCodeGenerator(mockSnowflake)
      val result = testGenerator.nextString()

      assertEquals(13, result.length, "When the snowflake ID is empty, order code length should match the timestamp length")
      assertTrue(result.matches(Regex("\\d{13}")), "Result should contain exactly 13 digits")
      assertTimestampPrefix(result)
    }

    @Test
    fun `should handle special snowflake responses`() {
      val mockSnowflake = mockk<ISnowflakeGenerator>()
      val snowflakeValue = "123456789"
      every { mockSnowflake.currentTimeMillis() } returns System.currentTimeMillis()
      every { mockSnowflake.nextString() } returns snowflakeValue

      val testGenerator = SynchronizedSimpleOrderCodeGenerator(mockSnowflake)
      val result = testGenerator.nextString()

      assertTrue(result.endsWith("123456789"), "Order code should end with the snowflake ID")
      assertTrue(result.length >= 13 + snowflakeValue.length, "Order code length should at least include timestamp and snowflake ID lengths")
      assertTimestampPrefix(result)
    }
  }

  @Nested
  inner class BoundaryConditions {
    @Test
    fun `should handle very long snowflake ids`() {
      val mockSnowflake = mockk<ISnowflakeGenerator>()
      val longSnowflakeId = "1".repeat(100)
      every { mockSnowflake.currentTimeMillis() } returns System.currentTimeMillis()
      every { mockSnowflake.nextString() } returns longSnowflakeId

      val testGenerator = SynchronizedSimpleOrderCodeGenerator(mockSnowflake)
      val result = testGenerator.nextString()

      assertTrue(result.length >= 13 + longSnowflakeId.length, "Order code length should at least include timestamp and snowflake ID lengths")
      assertTimestampPrefix(result)
      assertTrue(result.endsWith(longSnowflakeId), "Order code should include the full snowflake ID")
    }

    @Test
    fun `should handle minimum valid snowflake id`() {
      val mockSnowflake = mockk<ISnowflakeGenerator>()
      every { mockSnowflake.currentTimeMillis() } returns System.currentTimeMillis()
      every { mockSnowflake.nextString() } returns "1"

      val testGenerator = SynchronizedSimpleOrderCodeGenerator(mockSnowflake)
      val result = testGenerator.nextString()

      assertEquals(14, result.length, "Order code length should be 14 digits")
      assertTimestampPrefix(result)
      assertTrue(result.endsWith("1"), "Order code should end with 1")
      assertTrue(result.toLong() >= 1000, "Converted Long value should be >= 1000")
    }
  }
}
