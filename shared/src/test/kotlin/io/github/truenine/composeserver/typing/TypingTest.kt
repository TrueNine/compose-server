package io.github.truenine.composeserver.typing

import java.lang.reflect.Modifier
import kotlin.test.Test

class TypingTest {

  @Test
  fun `ensure enum method`() {
    val fns = PCB47::class.java.declaredMethods
    val static = fns.filter { it.name == "get" || it.name == "findVal" }.firstOrNull { Modifier.isStatic(it.modifiers) }
    println(static)
  }
}
