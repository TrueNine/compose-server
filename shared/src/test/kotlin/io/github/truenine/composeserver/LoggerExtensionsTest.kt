package io.github.truenine.composeserver

import org.slf4j.Logger
import kotlin.test.*

/**
 * Unit tests for LoggerExtensions.kt
 *
 * This test class verifies the functionality of all logger creation functions and ensures they return properly configured SLF4J logger instances.
 */
class LoggerExtensionsTest {

  @Test
  fun `test slf4j with Class parameter returns valid logger`() {
    // Given
    val clazz = LoggerExtensionsTest::class.java

    // When
    val logger = slf4j(clazz)

    // Then
    assertNotNull(logger, "Logger should not be null")
    assertEquals(clazz.name, logger.name, "Logger name should match the class name")
  }

  @Test
  fun `test slf4j with KClass parameter returns valid logger`() {
    // Given
    val kClass = LoggerExtensionsTest::class

    // When
    val logger = slf4j(kClass)

    // Then
    assertNotNull(logger, "Logger should not be null")
    assertEquals(kClass.java.name, logger.name, "Logger name should match the KClass name")
  }

  @Test
  fun `test slf4j with explicit SystemLogger class parameter`() {
    // When - call slf4j() with explicit SystemLogger::class parameter
    val logger = slf4j(SystemLogger::class)

    // Then
    assertNotNull(logger, "Logger should not be null")
    assertTrue(logger is Logger, "Logger should be an instance of SLF4J Logger")
    // SystemLogger is a typealias for Logger, so the logger name should be "org.slf4j.Logger"
    assertEquals(Logger::class.java.name, logger.name, "Logger name should match Logger class name")
  }

  @Test
  fun `test slf4j function overload resolution works correctly`() {
    // Test that different overloads work as expected
    val classLogger = slf4j(LoggerExtensionsTest::class.java)
    val kClassLogger = slf4j(LoggerExtensionsTest::class)
    val reifiedLogger = slf4j<LoggerExtensionsTest>()

    // All should have the same name since they refer to the same class
    assertEquals(LoggerExtensionsTest::class.java.name, classLogger.name)
    assertEquals(LoggerExtensionsTest::class.java.name, kClassLogger.name)
    assertEquals(LoggerExtensionsTest::class.java.name, reifiedLogger.name)
  }

  @Test
  fun `test slf4j with reified type parameter returns valid logger`() {
    // When
    val logger = slf4j<LoggerExtensionsTest>()

    // Then
    assertNotNull(logger, "Logger should not be null")
    assertEquals(LoggerExtensionsTest::class.java.name, logger.name, "Logger name should match the reified type name")
  }

  @Test
  fun `test logger function returns valid logger`() {
    // When
    val logger = logger<LoggerExtensionsTest>()

    // Then
    assertNotNull(logger, "Logger should not be null")
    assertEquals(LoggerExtensionsTest::class.java.name, logger.name, "Logger name should match the reified type name")
  }

  @Test
  fun `test logger and slf4j reified functions return equivalent loggers`() {
    // When
    val slf4jLogger = slf4j<LoggerExtensionsTest>()
    val loggerLogger = logger<LoggerExtensionsTest>()

    // Then
    assertEquals(slf4jLogger.name, loggerLogger.name, "Both functions should return loggers with the same name")
    assertEquals(slf4jLogger.javaClass, loggerLogger.javaClass, "Both functions should return the same type of logger")
  }

  @Test
  fun `test deprecated Slf4jKotlinAdaptor getLog with object returns valid logger`() {
    // Given
    val testObject = "test string"

    // When
    @Suppress("DEPRECATION") val logger = Slf4jKotlinAdaptor.getLog(testObject)

    // Then
    assertNotNull(logger, "Logger should not be null")
    assertEquals(String::class.java.name, logger.name, "Logger name should match the object's class name")
  }

  @Test
  fun `test different classes produce different logger names`() {
    // When
    val stringLogger = slf4j<String>()
    val intLogger = slf4j<Int>()
    val testLogger = slf4j<LoggerExtensionsTest>()

    // Then
    assertEquals("java.lang.String", stringLogger.name)
    assertEquals("java.lang.Integer", intLogger.name) // Int maps to Integer in JVM
    assertEquals(LoggerExtensionsTest::class.java.name, testLogger.name)

    // Ensure all names are different
    assertTrue(stringLogger.name != intLogger.name, "String and Int loggers should have different names")
    assertTrue(stringLogger.name != testLogger.name, "String and Test loggers should have different names")
    assertTrue(intLogger.name != testLogger.name, "Int and Test loggers should have different names")
  }

  @Test
  fun `test logger instances are properly configured for logging levels`() {
    // Given
    val logger = slf4j<LoggerExtensionsTest>()

    // Then - verify logger has standard SLF4J methods available
    // Note: We can't test actual logging output without complex setup,
    // but we can verify the methods exist and don't throw exceptions
    assertTrue(logger.isErrorEnabled || !logger.isErrorEnabled, "Error level check should not throw")
    assertTrue(logger.isWarnEnabled || !logger.isWarnEnabled, "Warn level check should not throw")
    assertTrue(logger.isInfoEnabled || !logger.isInfoEnabled, "Info level check should not throw")
    assertTrue(logger.isDebugEnabled || !logger.isDebugEnabled, "Debug level check should not throw")
    assertTrue(logger.isTraceEnabled || !logger.isTraceEnabled, "Trace level check should not throw")
  }

  @Test
  fun `test SystemLogger type alias works correctly`() {
    // When
    val logger: SystemLogger = slf4j<LoggerExtensionsTest>()

    // Then
    assertNotNull(logger, "SystemLogger should not be null")
  }
}
