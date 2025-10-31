package io.github.truenine.composeserver

import io.github.truenine.composeserver.testtoolkit.log
import java.util.concurrent.ConcurrentHashMap
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * Validates collection extension functions defined in CollectionExtensions.kt.
 */
class CollectionExtensionsTest {

  @Test
  fun createsConcurrentMapWithInitialValues() {
    val map = mutableLockMapOf("key1" to "value1", "key2" to "value2", "key3" to "value3")

    log.info("Created map type: {}", map::class.java.simpleName)
    log.info("Map size: {}", map.size)

    assertTrue(map is ConcurrentHashMap, "Should return a ConcurrentHashMap instance")
    assertEquals(3, map.size, "Map size should equal 3")
    assertEquals("value1", map["key1"], "Should contain expected entry for key1")
    assertEquals("value2", map["key2"], "Should contain expected entry for key2")
    assertEquals("value3", map["key3"], "Should contain expected entry for key3")
  }

  @Test
  fun createsEmptyConcurrentMap() {
    val map = mutableLockMapOf<String, Int>()

    log.info("Empty map type: {}", map::class.java.simpleName)
    log.info("Empty map size: {}", map.size)

    assertTrue(map is ConcurrentHashMap, "Should return a ConcurrentHashMap instance")
    assertEquals(0, map.size, "Empty map should have size 0")
    assertTrue(map.isEmpty(), "Map should be empty")
  }

  @Test
  fun validatesConcurrentBehaviour() {
    val map = mutableLockMapOf<String, Int>()

    // Simulate concurrent writes
    val threads =
      (1..10).map { threadIndex ->
        Thread {
          repeat(100) { iteration ->
            val key = "thread-$threadIndex-item-$iteration"
            map[key] = threadIndex * 100 + iteration
          }
        }
      }

    threads.forEach { it.start() }
    threads.forEach { it.join() }

    log.info("Map size after concurrent writes: {}", map.size)
    assertEquals(1000, map.size, "Should include every concurrently inserted element")
  }

  @Test
  fun runsBlockWhenCollectionNotEmpty() {
    val list = listOf("a", "b", "c")

    val result =
      list.isNotEmptyRun {
        log.info("Collection is not empty; executing block with size {}", size)
        this.joinToString(",")
      }

    assertNotNull(result, "Non-empty collection should execute block and return a result")
    assertEquals("a,b,c", result, "Should return the joined string")
  }

  @Test
  fun doesNotRunBlockWhenCollectionEmpty() {
    val emptyList = emptyList<String>()

    val result =
      emptyList.isNotEmptyRun {
        log.info("This block should not run")
        "should not execute"
      }

    assertNull(result, "Empty collection should return null")
  }

  @Test
  fun doesNotRunBlockWhenCollectionNull() {
    val nullList: List<String>? = null

    val result =
      nullList.isNotEmptyRun {
        log.info("This block should not run")
        "should not execute"
      }

    assertNull(result, "Null collection should return null")
  }

  @Test
  fun expandsPairToTriple() {
    val pair = "first" to "second"
    val triple = pair and "third"

    log.info("Original pair: {}", pair)
    log.info("Resulting triple: {}", triple)

    assertEquals("first", triple.first, "First element should match")
    assertEquals("second", triple.second, "Second element should match")
    assertEquals("third", triple.third, "Third element should match")
  }

  @Test
  fun expandsPairWithMixedTypes() {
    val pair = 1 to "string"
    val triple = pair and true

    log.info("Mixed-type triple: {}", triple)

    assertEquals(1, triple.first, "First element should be Int")
    assertEquals("string", triple.second, "Second element should be String")
    assertEquals(true, triple.third, "Third element should be Boolean")
  }

  @Test
  fun chainsCollectionExtensions() {
    val initialList = listOf(1, 2, 3, 4, 5)

    val result =
      initialList.isNotEmptyRun {
        log.info("Processing non-empty list, size {}", size)
        this.filter { it % 2 == 0 }.map { it * 2 }.joinToString(",")
      }

    assertNotNull(result, "Chained call should return a result")
    assertEquals("4,8", result, "Should filter even numbers and double them")
  }

  @Test
  fun validatesMutableLockMapOfCapacityScaling() {
    // Validate different initial sizes
    val smallMap = mutableLockMapOf("a" to 1)
    val mediumMap = mutableLockMapOf(*(1..10).map { "key$it" to it }.toTypedArray())
    val largeMap = mutableLockMapOf(*(1..100).map { "key$it" to it }.toTypedArray())

    log.info("Small map size: {}", smallMap.size)
    log.info("Medium map size: {}", mediumMap.size)
    log.info("Large map size: {}", largeMap.size)

    assertEquals(1, smallMap.size, "Small map should contain one element")
    assertEquals(10, mediumMap.size, "Medium map should contain ten elements")
    assertEquals(100, largeMap.size, "Large map should contain one hundred elements")

    // Verify contents are preserved
    assertTrue((1..10).all { mediumMap["key$it"] == it }, "Medium map should contain every expected entry")
    assertTrue((1..100).all { largeMap["key$it"] == it }, "Large map should contain every expected entry")
  }
}
