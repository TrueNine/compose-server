package net.yan100.compose.testtookit

import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertNotNull

/**
 * 确保能够启动
 */
class LaunchTest {
  private val logger = log

  @BeforeTest
  fun setup() {
    assertNotNull(logger)
  }

  @Test
  fun `test launch`() {
    log.info("launched")
  }
}
