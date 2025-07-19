package io.github.truenine.composeserver.gradleplugin.spotless

import kotlin.test.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class SpotlessConfigTest {

  private lateinit var config: SpotlessConfig

  @BeforeEach
  fun setup() {
    config = SpotlessConfig()
  }

  @Test
  fun `should have correct default values`() {
    assertEquals(false, config.enabled)
  }

  @Test
  fun `should allow configuration changes`() {
    config.enabled = true
    assertEquals(true, config.enabled)

    config.enabled = false
    assertEquals(false, config.enabled)
  }
}
