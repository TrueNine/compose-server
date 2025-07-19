package io.github.truenine.composeserver.gradleplugin.dotenv

import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class DotenvConfigTest {

  private lateinit var config: DotenvConfig

  @BeforeEach
  fun setup() {
    config = DotenvConfig()
  }

  @Test
  fun `should have correct default values`() {
    assertFalse(config.enabled)
    assertEquals("", config.filePath)
    assertTrue(config.warnOnMissingFile)
    assertTrue(config.verboseErrors)
    assertFalse(config.overrideExisting)
    assertFalse(config.ignoreEmptyValues)
    assertNull(config.prefixFilter)
    assertTrue(config.excludeKeys.isEmpty())
    assertTrue(config.includeKeys.isEmpty())
  }

  @Test
  fun `should set file path correctly`() {
    config.filePath(".env")
    assertEquals(".env", config.filePath)

    config.filePath("/absolute/path/.env")
    assertEquals("/absolute/path/.env", config.filePath)
  }

  @Test
  fun `should configure warning options`() {
    config.warnOnMissingFile(false)
    assertFalse(config.warnOnMissingFile)

    config.verboseErrors(false)
    assertFalse(config.verboseErrors)
  }

  @Test
  fun `should configure override and ignore options`() {
    config.overrideExisting(true)
    assertTrue(config.overrideExisting)

    config.ignoreEmptyValues(true)
    assertTrue(config.ignoreEmptyValues)
  }

  @Test
  fun `should set prefix filter`() {
    config.prefixFilter("APP_")
    assertEquals("APP_", config.prefixFilter)

    config.prefixFilter(null)
    assertNull(config.prefixFilter)
  }

  @Test
  fun `should manage exclude keys`() {
    config.excludeKeys("KEY1", "KEY2", "KEY3")
    assertEquals(setOf("KEY1", "KEY2", "KEY3"), config.excludeKeys)

    config.excludeKeys("KEY4")
    assertEquals(setOf("KEY1", "KEY2", "KEY3", "KEY4"), config.excludeKeys)

    config.clearExcludeKeys()
    assertTrue(config.excludeKeys.isEmpty())
  }

  @Test
  fun `should manage include keys`() {
    config.includeKeys("KEY1", "KEY2", "KEY3")
    assertEquals(setOf("KEY1", "KEY2", "KEY3"), config.includeKeys)

    config.includeKeys("KEY4")
    assertEquals(setOf("KEY1", "KEY2", "KEY3", "KEY4"), config.includeKeys)

    config.clearIncludeKeys()
    assertTrue(config.includeKeys.isEmpty())
  }

  @Test
  fun `should validate configuration correctly`() {
    // Invalid: disabled
    assertFalse(config.isValid())

    // Invalid: enabled but no file path
    config.enabled = true
    assertFalse(config.isValid())

    // Invalid: enabled but blank file path
    config.filePath = "   "
    assertFalse(config.isValid())

    // Valid: enabled with file path
    config.filePath = ".env"
    assertTrue(config.isValid())
  }

  @Test
  fun `should generate correct summary`() {
    config.enabled = true
    config.filePath = ".env"
    config.warnOnMissingFile = false
    config.verboseErrors = false
    config.overrideExisting = true
    config.ignoreEmptyValues = true
    config.prefixFilter = "APP_"
    config.excludeKeys("SECRET")
    config.includeKeys("DB_HOST", "DB_PORT")

    val summary = config.getSummary()

    assertTrue(summary.contains("enabled=true"))
    assertTrue(summary.contains("filePath='.env'"))
    assertTrue(summary.contains("warnOnMissingFile=false"))
    assertTrue(summary.contains("verboseErrors=false"))
    assertTrue(summary.contains("overrideExisting=true"))
    assertTrue(summary.contains("ignoreEmptyValues=true"))
    assertTrue(summary.contains("prefixFilter='APP_'"))
    assertTrue(summary.contains("excludeKeys=[SECRET]"))
    assertTrue(summary.contains("includeKeys=[DB_HOST, DB_PORT]"))
  }

  @Test
  fun `should generate minimal summary for default config`() {
    val summary = config.getSummary()

    assertTrue(summary.contains("enabled=false"))
    assertTrue(summary.contains("filePath=''"))
    assertTrue(summary.contains("warnOnMissingFile=true"))
    assertTrue(summary.contains("verboseErrors=true"))
    assertTrue(summary.contains("overrideExisting=false"))
    assertTrue(summary.contains("ignoreEmptyValues=false"))
    assertFalse(summary.contains("prefixFilter="))
    assertFalse(summary.contains("excludeKeys="))
    assertFalse(summary.contains("includeKeys="))
  }
}
