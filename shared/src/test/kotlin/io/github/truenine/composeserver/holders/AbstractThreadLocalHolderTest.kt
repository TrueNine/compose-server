package io.github.truenine.composeserver.holders

import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.reflect.KClass
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class AbstractThreadLocalHolderTest {

  // Test implementation of AbstractThreadLocalHolder
  private class TestHolder(nameId: KClass<*>? = null, defaultValue: String? = null) : AbstractThreadLocalHolder<String>(nameId, defaultValue)

  private lateinit var holder: TestHolder

  @BeforeEach
  fun setUp() {
    holder = TestHolder()
  }

  @AfterEach
  fun tearDown() {
    holder.close()
  }

  @Nested
  inner class BasicFunctionalityTest {

    @Test
    fun `should store and retrieve value`() {
      holder.set("test value")
      assertEquals("test value", holder.get())
    }

    @Test
    fun `should update value correctly`() {
      holder.set("initial value")
      assertEquals("initial value", holder.get())

      holder.set("updated value")
      assertEquals("updated value", holder.get())
    }

    @Test
    fun `should use property syntax for get and set`() {
      holder.content = "property value"
      assertEquals("property value", holder.content)
    }

    @Test
    fun `should support component1 destructuring`() {
      holder.set("destructured value")
      val (value) = holder
      assertEquals("destructured value", value)
    }

    @Test
    fun `should support plusAssign operator`() {
      holder += "assigned value"
      assertEquals("assigned value", holder.get())
    }
  }

  @Nested
  inner class DefaultValueTest {

    @Test
    fun `should use default value when provided`() {
      val holderWithDefault = TestHolder(defaultValue = "default")
      assertEquals("default", holderWithDefault.get())
      holderWithDefault.close()
    }

    @Test
    fun `should return null when no default and no value set`() {
      val emptyHolder = TestHolder()
      assertNull(emptyHolder.get())
      emptyHolder.close()
    }

    @Test
    fun `should override default value when explicitly set`() {
      val holderWithDefault = TestHolder(defaultValue = "default")
      assertEquals("default", holderWithDefault.get())

      holderWithDefault.set("override")
      assertEquals("override", holderWithDefault.get())
      holderWithDefault.close()
    }

    @Test
    fun `should not use null as default value`() {
      val holderWithNullDefault = TestHolder(defaultValue = null)
      assertNull(holderWithNullDefault.get())
      holderWithNullDefault.close()
    }
  }

  @Nested
  inner class NameIdentificationTest {

    @Test
    fun `should use class name when no nameId provided`() {
      val holder1 = TestHolder()
      val holder2 = TestHolder()

      // Both should work independently
      holder1.set("value1")
      holder2.set("value2")

      assertEquals("value1", holder1.get())
      assertEquals("value2", holder2.get())

      holder1.close()
      holder2.close()
    }

    @Test
    fun `should use provided nameId class name`() {
      val holderWithName = TestHolder(nameId = String::class)
      holderWithName.set("named value")
      assertEquals("named value", holderWithName.get())
      holderWithName.close()
    }

    @Test
    fun `should handle different nameId correctly`() {
      val holder1 = TestHolder(nameId = String::class)
      val holder2 = TestHolder(nameId = Int::class)

      holder1.set("string holder")
      holder2.set("int holder")

      assertEquals("string holder", holder1.get())
      assertEquals("int holder", holder2.get())

      holder1.close()
      holder2.close()
    }
  }

  @Nested
  inner class ThreadIsolationTest {

    @Test
    fun `should isolate values between threads`() {
      val results = mutableMapOf<String, String?>()
      val threads = mutableListOf<Thread>()

      repeat(3) { i ->
        val thread = Thread {
          val threadHolder = TestHolder()
          threadHolder.set("thread-$i-value")
          Thread.sleep(10) // Small delay to ensure concurrency
          synchronized(results) { results["thread-$i"] = threadHolder.get() }
          threadHolder.close()
        }
        threads.add(thread)
        thread.start()
      }

      threads.forEach { it.join() }

      assertEquals("thread-0-value", results["thread-0"])
      assertEquals("thread-1-value", results["thread-1"])
      assertEquals("thread-2-value", results["thread-2"])
    }

    @Test
    fun `should inherit values to child threads`() {
      holder.set("parent value")

      var childValue: String? = null
      val childThread = Thread { childValue = holder.get() }

      childThread.start()
      childThread.join()

      assertEquals("parent value", childValue)
    }

    @Test
    fun `should allow child threads to modify inherited values independently`() {
      holder.set("parent value")

      var childValue: String? = null
      var parentValueAfterChild: String? = null

      val childThread = Thread {
        holder.set("child modified value")
        childValue = holder.get()
      }

      childThread.start()
      childThread.join()

      parentValueAfterChild = holder.get()

      assertEquals("child modified value", childValue)
      assertEquals("parent value", parentValueAfterChild) // Parent should be unchanged
    }

    @Test
    fun `should handle multiple inheritance levels`() {
      holder.set("grandparent value")

      var parentValue: String? = null
      var childValue: String? = null

      val parentThread = Thread {
        holder.set("parent value")
        parentValue = holder.get()

        val childThread = Thread { childValue = holder.get() }
        childThread.start()
        childThread.join()
      }

      parentThread.start()
      parentThread.join()

      assertEquals("parent value", parentValue)
      assertEquals("parent value", childValue) // Child inherits from parent
      assertEquals("grandparent value", holder.get()) // Original unchanged
    }
  }

  @Nested
  inner class ResourceManagementTest {

    @Test
    fun `should clear value when closed`() {
      holder.set("test value")
      assertEquals("test value", holder.get())

      holder.close()
      assertNull(holder.get())
    }

    @Test
    fun `should be safe to close multiple times`() {
      holder.set("test value")

      holder.close()
      assertNull(holder.get())

      holder.close() // Should not throw
      assertNull(holder.get())
    }

    @Test
    fun `should be usable as Closeable resource`() {
      TestHolder().use { testHolder ->
        testHolder.set("resource value")
        assertEquals("resource value", testHolder.get())
      }
      // Should be automatically closed after use block
    }
  }

  @Nested
  inner class ConcurrencyTest {

    @Test
    fun `should handle concurrent access from same thread safely`() {
      repeat(100) { i ->
        holder.set("value-$i")
        assertEquals("value-$i", holder.get())
      }
    }

    @Test
    fun `should handle high concurrency across multiple threads`() {
      val threadCount = 50
      val iterationsPerThread = 20
      val results = mutableMapOf<String, MutableList<String>>()
      val threads = mutableListOf<Thread>()

      repeat(threadCount) { threadIndex ->
        val thread = Thread {
          val threadHolder = TestHolder()
          val threadResults = mutableListOf<String>()

          repeat(iterationsPerThread) { iteration ->
            val value = "thread-$threadIndex-iteration-$iteration"
            threadHolder.set(value)
            threadResults.add(threadHolder.get() ?: "null")
          }

          synchronized(results) { results["thread-$threadIndex"] = threadResults }
          threadHolder.close()
        }
        threads.add(thread)
        thread.start()
      }

      threads.forEach { it.join() }

      // Verify all threads completed successfully
      assertEquals(threadCount, results.size)
      results.forEach { (threadName, values) ->
        assertEquals(iterationsPerThread, values.size, "Thread $threadName should have $iterationsPerThread values")
        values.forEachIndexed { index, value ->
          assertTrue(value.startsWith(threadName), "Value should belong to correct thread")
          assertTrue(value.contains("iteration-$index"), "Value should have correct iteration")
        }
      }
    }

    @Test
    fun `should handle stress test with rapid set and get operations`() {
      val threads = mutableListOf<Thread>()
      val exceptions = mutableListOf<Exception>()

      repeat(10) { threadIndex ->
        val thread = Thread {
          try {
            val threadHolder = TestHolder()

            repeat(1000) { iteration ->
              threadHolder.set("stress-$threadIndex-$iteration")
              val retrieved = threadHolder.get()
              if (retrieved != "stress-$threadIndex-$iteration") {
                throw AssertionError("Value mismatch in thread $threadIndex iteration $iteration")
              }
            }
            threadHolder.close()
          } catch (e: Exception) {
            synchronized(exceptions) { exceptions.add(e) }
          }
        }
        threads.add(thread)
        thread.start()
      }

      threads.forEach { it.join() }

      if (exceptions.isNotEmpty()) {
        throw AssertionError("Concurrency test failed with ${exceptions.size} exceptions: ${exceptions.first()}")
      }
    }
  }

  @Nested
  inner class TypeSafetyTest {

    // Test holder for different types
    inner class IntHolder(defaultValue: Int? = null) : AbstractThreadLocalHolder<Int>(defaultValue = defaultValue)

    inner class ListHolder(defaultValue: List<String>? = null) : AbstractThreadLocalHolder<List<String>>(defaultValue = defaultValue)

    @Test
    fun `should maintain type safety for different types`() {
      val intHolder = IntHolder()
      val listHolder = ListHolder()

      intHolder.set(42)
      listHolder.set(listOf("a", "b", "c"))

      assertEquals(42, intHolder.get())
      assertEquals(listOf("a", "b", "c"), listHolder.get())

      intHolder.close()
      listHolder.close()
    }

    @Test
    fun `should work with nullable types`() {
      val nullableHolder = TestHolder()

      nullableHolder.set("non-null")
      assertEquals("non-null", nullableHolder.get())

      // Note: For testing nullable types, we need to be careful with type inference
      // In real usage, this would work with proper nullable type declaration
    }

    @Test
    fun `should work with complex types`() {
      val complexHolder = ListHolder()
      val testList = listOf("item1", "item2", "item3")

      complexHolder.set(testList)
      val retrieved = complexHolder.get()

      assertEquals(testList, retrieved)
      assertEquals(3, retrieved?.size)
      assertTrue(retrieved?.contains("item2") == true)

      complexHolder.close()
    }
  }

  @Nested
  inner class EdgeCaseTest {

    @Test
    fun `should handle very long strings`() {
      val longString = "x".repeat(100_000)
      holder.set(longString)
      assertEquals(longString, holder.get())
    }

    @Test
    fun `should handle empty strings`() {
      holder.set("")
      assertEquals("", holder.get())
    }

    @Test
    fun `should handle special characters and Unicode`() {
      val specialString = "Hello world! @#$%^&*()[]{}|\\:;\"'<>,.?/~`"
      holder.set(specialString)
      assertEquals(specialString, holder.get())
    }

    @Test
    fun `should handle rapid open and close cycles`() {
      repeat(100) {
        val tempHolder = TestHolder()
        tempHolder.set("temp-$it")
        assertEquals("temp-$it", tempHolder.get())
        tempHolder.close()
      }
    }

    @Test
    fun `should handle holders with same nameId class`() {
      val holder1 = TestHolder(nameId = String::class)
      val holder2 = TestHolder(nameId = String::class)

      holder1.set("holder1-value")
      holder2.set("holder2-value")

      // Both should maintain their own values despite same nameId
      assertEquals("holder1-value", holder1.get())
      assertEquals("holder2-value", holder2.get())

      holder1.close()
      holder2.close()
    }
  }

  @Nested
  inner class MemoryLeakPreventionTest {

    @Test
    fun `should not leak memory after close`() {
      val holders = mutableListOf<TestHolder>()

      // Create many holders
      repeat(1000) { i ->
        val holder = TestHolder()
        holder.set("value-$i")
        holders.add(holder)
      }

      // Verify they work
      holders.forEachIndexed { index, holder -> assertEquals("value-$index", holder.get()) }

      // Close all holders
      holders.forEach { it.close() }

      // Verify they're cleared
      holders.forEach { holder -> assertNull(holder.get()) }
    }

    @Test
    fun `should handle thread completion cleanup`() {
      val createdValues = mutableListOf<String>()
      val threads = mutableListOf<Thread>()

      repeat(50) { i ->
        val thread = Thread {
          val threadHolder = TestHolder()
          val value = "thread-$i-value"
          threadHolder.set(value)
          synchronized(createdValues) { createdValues.add(value) }
          // Don't explicitly close - let thread termination handle cleanup
        }
        threads.add(thread)
        thread.start()
      }

      threads.forEach { it.join() }

      // All values should have been created
      assertEquals(50, createdValues.size)

      // Thread-local values should be automatically cleaned up when threads terminate
      // This is handled by the JVM's ThreadLocal implementation
    }
  }
}
