package io.github.truenine.composeserver.holders

import io.mockk.mockk
import io.mockk.verify
import kotlin.test.*
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.context.ApplicationEventPublisher

class EventPublisherHolderTest {

  private lateinit var mockPublisher: ApplicationEventPublisher

  @BeforeEach
  fun setUp() {
    mockPublisher = mockk(relaxed = true)
    EventPublisherHolder.close() // Clear any previous state
  }

  @AfterEach
  fun tearDown() {
    EventPublisherHolder.close() // Clean up after each test
  }

  @Nested
  inner class BasicFunctionalityTest {

    @Test
    fun `should store and retrieve event publisher`() {
      EventPublisherHolder.set(mockPublisher)
      assertEquals(mockPublisher, EventPublisherHolder.get())
    }

    @Test
    fun `should update event publisher correctly`() {
      val firstPublisher = mockk<ApplicationEventPublisher>(relaxed = true)
      val secondPublisher = mockk<ApplicationEventPublisher>(relaxed = true)

      EventPublisherHolder.set(firstPublisher)
      assertEquals(firstPublisher, EventPublisherHolder.get())

      EventPublisherHolder.set(secondPublisher)
      assertEquals(secondPublisher, EventPublisherHolder.get())
    }

    @Test
    fun `should use property syntax for get and set`() {
      EventPublisherHolder.content = mockPublisher
      assertEquals(mockPublisher, EventPublisherHolder.content)
    }

    @Test
    fun `should support component1 destructuring`() {
      EventPublisherHolder.set(mockPublisher)
      val (publisher) = EventPublisherHolder
      assertEquals(mockPublisher, publisher)
    }

    @Test
    fun `should support plusAssign operator`() {
      EventPublisherHolder += mockPublisher
      assertEquals(mockPublisher, EventPublisherHolder.get())
    }
  }

  @Nested
  inner class ThreadLocalBehaviorTest {

    @Test
    fun `should return null when no publisher is set`() {
      assertNull(EventPublisherHolder.get())
    }

    @Test
    fun `should clear publisher when closed`() {
      EventPublisherHolder.set(mockPublisher)
      assertEquals(mockPublisher, EventPublisherHolder.get())

      EventPublisherHolder.close()
      assertNull(EventPublisherHolder.get())
    }

    @Test
    fun `should be safe to close multiple times`() {
      EventPublisherHolder.set(mockPublisher)

      EventPublisherHolder.close()
      assertNull(EventPublisherHolder.get())

      EventPublisherHolder.close() // Should not throw
      assertNull(EventPublisherHolder.get())
    }

    @Test
    fun `should isolate publishers between threads`() {
      val mainThreadPublisher = mockk<ApplicationEventPublisher>(relaxed = true)
      val otherThreadPublisher = mockk<ApplicationEventPublisher>(relaxed = true)

      EventPublisherHolder.set(mainThreadPublisher)
      assertEquals(mainThreadPublisher, EventPublisherHolder.get())

      var otherThreadResult: ApplicationEventPublisher? = null
      var mainThreadResultAfter: ApplicationEventPublisher? = null

      val otherThread = Thread {
        EventPublisherHolder.set(otherThreadPublisher)
        otherThreadResult = EventPublisherHolder.get()
      }

      otherThread.start()
      otherThread.join()

      mainThreadResultAfter = EventPublisherHolder.get()

      assertEquals(otherThreadPublisher, otherThreadResult)
      assertEquals(mainThreadPublisher, mainThreadResultAfter)
    }

    @Test
    fun `should inherit publisher to child threads`() {
      EventPublisherHolder.set(mockPublisher)

      var childThreadPublisher: ApplicationEventPublisher? = null
      val childThread = Thread { childThreadPublisher = EventPublisherHolder.get() }

      childThread.start()
      childThread.join()

      assertEquals(mockPublisher, childThreadPublisher)
    }

    @Test
    fun `should allow child threads to modify inherited publisher independently`() {
      val parentPublisher = mockk<ApplicationEventPublisher>(relaxed = true)
      val childPublisher = mockk<ApplicationEventPublisher>(relaxed = true)

      EventPublisherHolder.set(parentPublisher)

      var childThreadResult: ApplicationEventPublisher? = null
      var parentResultAfterChild: ApplicationEventPublisher? = null

      val childThread = Thread {
        EventPublisherHolder.set(childPublisher)
        childThreadResult = EventPublisherHolder.get()
      }

      childThread.start()
      childThread.join()

      parentResultAfterChild = EventPublisherHolder.get()

      assertEquals(childPublisher, childThreadResult)
      assertEquals(parentPublisher, parentResultAfterChild) // Parent should be unchanged
    }
  }

  @Nested
  inner class ConcurrencyTest {

    @Test
    fun `should handle concurrent access from multiple threads safely`() {
      val threadCount = 20
      val publishers = mutableMapOf<String, ApplicationEventPublisher>()
      val results = mutableMapOf<String, ApplicationEventPublisher?>()
      val threads = mutableListOf<Thread>()

      // Create unique publishers for each thread
      repeat(threadCount) { i -> publishers["thread-$i"] = mockk<ApplicationEventPublisher>(relaxed = true) }

      repeat(threadCount) { i ->
        val thread = Thread {
          val threadPublisher = publishers["thread-$i"]!!
          EventPublisherHolder.set(threadPublisher)
          Thread.sleep(10) // Small delay to ensure concurrency
          synchronized(results) { results["thread-$i"] = EventPublisherHolder.get() }
        }
        threads.add(thread)
        thread.start()
      }

      threads.forEach { it.join() }

      // Verify each thread got its own publisher
      assertEquals(threadCount, results.size)
      results.forEach { (threadName, result) -> assertEquals(publishers[threadName], result, "Thread $threadName should have its own publisher") }
    }

    @Test
    fun `should handle rapid set and get operations concurrently`() {
      val iterations = 100
      val threads = mutableListOf<Thread>()
      val exceptions = mutableListOf<Exception>()

      repeat(5) { threadIndex ->
        val thread = Thread {
          try {
            val threadPublisher = mockk<ApplicationEventPublisher>(relaxed = true)

            repeat(iterations) { iteration ->
              EventPublisherHolder.set(threadPublisher)
              val retrieved = EventPublisherHolder.get()
              if (retrieved != threadPublisher) {
                throw AssertionError("Publisher mismatch in thread $threadIndex iteration $iteration")
              }
            }
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

    @Test
    fun `should handle stress test with many threads`() {
      val threadCount = 50
      val threads = mutableListOf<Thread>()
      val completedThreads = mutableSetOf<String>()

      repeat(threadCount) { i ->
        val thread = Thread {
          val threadPublisher = mockk<ApplicationEventPublisher>(relaxed = true)

          // Multiple operations per thread
          repeat(10) {
            EventPublisherHolder.set(threadPublisher)
            val retrieved = EventPublisherHolder.get()
            assertEquals(threadPublisher, retrieved)
          }

          synchronized(completedThreads) { completedThreads.add("thread-$i") }
        }
        threads.add(thread)
        thread.start()
      }

      threads.forEach { it.join() }

      assertEquals(threadCount, completedThreads.size, "All threads should complete successfully")
    }
  }

  @Nested
  inner class ObjectBehaviorTest {

    @Test
    fun `should be a singleton object`() {
      val holder1 = EventPublisherHolder
      val holder2 = EventPublisherHolder

      assertTrue(holder1 === holder2, "EventPublisherHolder should be a singleton")
    }

    @Test
    fun `should maintain state across different references`() {
      val holder1 = EventPublisherHolder
      val holder2 = EventPublisherHolder

      holder1.set(mockPublisher)
      assertEquals(mockPublisher, holder2.get())

      holder2.close()
      assertNull(holder1.get())
    }

    @Test
    fun `should work with function references`() {
      fun setPublisher(publisher: ApplicationEventPublisher) {
        EventPublisherHolder.set(publisher)
      }

      fun getPublisher(): ApplicationEventPublisher? {
        return EventPublisherHolder.get()
      }

      setPublisher(mockPublisher)
      assertEquals(mockPublisher, getPublisher())
    }
  }

  @Nested
  inner class IntegrationTest {

    @Test
    fun `should work with real Spring event publishing patterns`() {
      // This test simulates how EventPublisherHolder would be used in practice

      // Simulate Spring setting the publisher in the holder
      EventPublisherHolder.set(mockPublisher)

      // Simulate a service using the holder to publish events
      fun publishEvent(event: Any) {
        val publisher = EventPublisherHolder.get()
        publisher?.publishEvent(event)
      }

      val testEvent = "test event"
      publishEvent(testEvent)

      verify { mockPublisher.publishEvent(testEvent) }
    }

    @Test
    fun `should handle null publisher gracefully in publishing scenario`() {
      // No publisher set
      assertNull(EventPublisherHolder.get())

      // Should not throw when trying to use null publisher
      val publisher = EventPublisherHolder.get()
      assertNull(publisher)

      // Real code should check for null before using
      fun safePublishEvent(event: Any) {
        EventPublisherHolder.get()?.publishEvent(event)
      }

      // Should not throw
      try {
        safePublishEvent("test event")
        // Test passes if no exception is thrown
      } catch (e: Exception) {
        fail("Should not throw exception: $e")
      }
    }

    @Test
    fun `should support event publishing from different threads with inherited context`() {
      EventPublisherHolder.set(mockPublisher)

      val publishedEvents = mutableListOf<String>()
      val threads = mutableListOf<Thread>()

      repeat(3) { i ->
        val thread = Thread {
          val publisher = EventPublisherHolder.get()
          val event = "event-from-thread-$i"
          publisher?.publishEvent(event)
          synchronized(publishedEvents) { publishedEvents.add(event) }
        }
        threads.add(thread)
        thread.start()
      }

      threads.forEach { it.join() }

      assertEquals(3, publishedEvents.size)
      publishedEvents.forEach { event -> verify { mockPublisher.publishEvent(event) } }
    }
  }

  @Nested
  inner class MemoryManagementTest {

    @Test
    fun `should not cause memory leaks when used across many threads`() {
      val threadCount = 100
      val threads = mutableListOf<Thread>()

      repeat(threadCount) { i ->
        val thread = Thread {
          val publisher = mockk<ApplicationEventPublisher>(relaxed = true)
          EventPublisherHolder.set(publisher)
          // Don't explicitly close - let thread termination handle cleanup
        }
        threads.add(thread)
        thread.start()
      }

      threads.forEach { it.join() }

      // Main thread should still have its own clean state
      assertNull(EventPublisherHolder.get())
    }

    @Test
    fun `should properly clean up when explicitly closed`() {
      val publishers = List(50) { mockk<ApplicationEventPublisher>(relaxed = true) }

      // Set and clear many publishers
      publishers.forEach { publisher ->
        EventPublisherHolder.set(publisher)
        assertEquals(publisher, EventPublisherHolder.get())
        EventPublisherHolder.close()
        assertNull(EventPublisherHolder.get())
      }
    }
  }

  @Nested
  inner class EdgeCaseTest {

    @Test
    fun `should handle multiple inheritance levels correctly`() {
      EventPublisherHolder.set(mockPublisher)

      var level1Result: ApplicationEventPublisher? = null
      var level2Result: ApplicationEventPublisher? = null

      val level1Thread = Thread {
        val level1Publisher = mockk<ApplicationEventPublisher>(relaxed = true)
        EventPublisherHolder.set(level1Publisher)
        level1Result = EventPublisherHolder.get()

        val level2Thread = Thread { level2Result = EventPublisherHolder.get() }
        level2Thread.start()
        level2Thread.join()
      }

      level1Thread.start()
      level1Thread.join()

      // Level 2 should inherit from Level 1, not from main thread
      assertEquals(level1Result, level2Result)
      assertNotEquals(mockPublisher, level2Result)

      // Main thread should still have original publisher
      assertEquals(mockPublisher, EventPublisherHolder.get())
    }

    @Test
    fun `should handle rapid thread creation and destruction`() {
      EventPublisherHolder.set(mockPublisher)

      repeat(100) { i ->
        val quickThread = Thread {
          val inherited = EventPublisherHolder.get()
          assertEquals(mockPublisher, inherited)
        }
        quickThread.start()
        quickThread.join() // Wait for completion before creating next
      }

      // Main thread should still have original publisher
      assertEquals(mockPublisher, EventPublisherHolder.get())
    }

    @Test
    fun `should handle thread interruption gracefully`() {
      EventPublisherHolder.set(mockPublisher)

      val interruptedThread = Thread {
        try {
          EventPublisherHolder.set(mockk<ApplicationEventPublisher>(relaxed = true))
          Thread.sleep(1000) // This will be interrupted
        } catch (e: InterruptedException) {
          // Thread was interrupted, but holder should still work
          val publisher = EventPublisherHolder.get()
          assertNotNull(publisher)
        }
      }

      interruptedThread.start()
      Thread.sleep(50) // Let thread start
      interruptedThread.interrupt()
      interruptedThread.join()

      // Main thread should be unaffected
      assertEquals(mockPublisher, EventPublisherHolder.get())
    }

    @Test
    fun `should handle close during concurrent access`() {
      EventPublisherHolder.set(mockPublisher)

      val accessThread = Thread {
        repeat(100) {
          try {
            val publisher = EventPublisherHolder.get()
            // May be null if close() was called, which is expected
          } catch (e: Exception) {
            // Should not throw exceptions
            fail("Unexpected exception during concurrent access: $e")
          }
          Thread.sleep(1)
        }
      }

      accessThread.start()

      // Close while other thread is accessing
      Thread.sleep(50)
      EventPublisherHolder.close()

      accessThread.join()

      assertNull(EventPublisherHolder.get())
    }
  }
}
