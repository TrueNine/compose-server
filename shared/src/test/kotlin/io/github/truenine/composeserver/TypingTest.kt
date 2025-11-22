package io.github.truenine.composeserver

import io.github.truenine.composeserver.enums.PCB47
import io.github.truenine.composeserver.testtoolkit.log
import java.lang.reflect.Modifier
import kotlin.test.Test

/**
 * # Type system tests
 *
 * Test the correctness of type-related functionality
 */
class TypingTest {

  @Test
  fun `test enum method existence`() {
    val fns = PCB47::class.java.declaredMethods
    val static = fns.filter { it.name == "get" || it.name == "findVal" }.firstOrNull { Modifier.isStatic(it.modifiers) }
    log.info("Found static method: {}", static)
  }
}
