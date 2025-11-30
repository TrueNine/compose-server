package io.github.truenine.composeserver.testtoolkit

import kotlin.test.*
import org.slf4j.LoggerFactory

/**
 * SLF4J function tests.
 *
 * Verifies the logging extension functions defined in LoggerExtensions.kt.
 *
 * @author TrueNine
 * @since 2025-07-12
 */
class LoggerExtensionsTest {

  val testProperty: String = "test value"
  val testNumber: Int = 42

  @Test
  fun logExtensionPropertyShouldReturnCorrectLogger() {
    val logger = this.log
    assertNotNull(logger)
    assertEquals(LoggerExtensionsTest::class.java.name, logger.name)

    // verify consistency with LoggerFactory
    val directLogger = LoggerFactory.getLogger(LoggerExtensionsTest::class.java)
    assertEquals(logger.name, directLogger.name)
  }

  @Test
  fun logExtensionPropertyShouldWorkForDifferentTypes() {
    // test various object types
    assertEquals(String::class.java.name, "test".log.name)
    assertEquals(Integer::class.java.name, 42.log.name)

    val testObject = TestClass()
    assertEquals(TestClass::class.java.name, testObject.log.name)
  }

  @Test
  fun loggerInfoExtensionShouldPrintPropertyValues() {
    // test KCallable extension function
    log.info(this::testProperty)
    log.info(this::testNumber)

    // test with custom logger instance
    val testObject = TestClass()
    testObject.log.info(testObject::name)
    testObject.log.info(testObject::value)
  }

  class TestClass {
    val name: String = "TestClass"
    val value: Int = 100
  }
}
