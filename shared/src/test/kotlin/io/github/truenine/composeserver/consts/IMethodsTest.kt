package io.github.truenine.composeserver.consts

import io.github.truenine.composeserver.testtoolkit.log
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/** Validates HTTP method constants declared in {@link IMethods}. */
class IMethodsTest {

  @Test
  fun verifiesHttpMethodConstants() {
    log.info("Verifying HTTP method constant values")

    assertEquals("GET", IMethods.GET)
    assertEquals("POST", IMethods.POST)
    assertEquals("PUT", IMethods.PUT)
    assertEquals("DELETE", IMethods.DELETE)
    assertEquals("OPTIONS", IMethods.OPTIONS)
    assertEquals("PATCH", IMethods.PATCH)
    assertEquals("HEAD", IMethods.HEAD)
    assertEquals("TRACE", IMethods.TRACE)

    log.info("All HTTP method constants verified")
  }

  @Test
  fun returnsAllHttpMethods() {
    log.info("Validating all() returns every HTTP method")

    val allMethods = IMethods.all()

    assertEquals(8, allMethods.size, "Should include eight HTTP methods")

    val expectedMethods = arrayOf(IMethods.GET, IMethods.POST, IMethods.PUT, IMethods.DELETE, IMethods.OPTIONS, IMethods.PATCH, IMethods.HEAD, IMethods.TRACE)

    expectedMethods.forEach { method -> assertTrue(allMethods.contains(method), "all() should contain: $method") }

    log.info("all() returned HTTP methods: {}", allMethods.contentToString())
  }

  @Test
  fun ensuresAllMethodReturnsDefensiveCopy() {
    log.info("Validating immutability of all() result")

    val methods1 = IMethods.all()
    val methods2 = IMethods.all()

    // Mutating the first array should not affect the second
    methods1[0] = "MODIFIED"

    assertEquals("GET", methods2[0], "Mutating one array should not affect the other")

    log.info("all() returned independent arrays")
  }
}
