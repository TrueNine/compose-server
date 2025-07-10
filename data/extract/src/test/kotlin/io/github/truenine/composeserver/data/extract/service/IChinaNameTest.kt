package io.github.truenine.composeserver.data.extract.service

import io.github.truenine.composeserver.testtoolkit.log
import kotlin.test.Test
import org.springframework.core.io.ClassPathResource

class IChinaNameTest {
  @Test
  fun `test get all name`() {
    val text = ClassPathResource("names.txt").file.readText().replace("\\((.*?)\\)".toRegex(), ",")
    val a = text.split(",").map { it.trim() }.distinct()
    val b = a.filter { it.length == 1 }.joinToString { "\n\"$it\"" }
    val c = a.filter { it.length == 2 }.joinToString { "\n\"$it\"" }
    log.info("b: {}", b)
    log.info("c: {}", c)
  }
}
