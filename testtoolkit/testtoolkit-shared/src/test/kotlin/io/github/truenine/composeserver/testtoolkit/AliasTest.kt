package io.github.truenine.composeserver.testtoolkit

import java.nio.file.Path
import kotlin.test.*
import org.junit.jupiter.api.io.TempDir
import org.slf4j.LoggerFactory
import org.springframework.test.annotation.Rollback

/**
 * Type alias tests.
 *
 * Verifies that the type aliases defined in Alias.kt map to the correct underlying types.
 *
 * @author TrueNine
 * @since 2025-07-12
 */
class AliasTest {

  @Test
  fun typeAliasesShouldMapToCorrectTypes() {
    // validate SystemTestLogger alias
    val logger: SystemTestLogger = LoggerFactory.getLogger(AliasTest::class.java)
    assertNotNull(logger)
    assertEquals("org.slf4j.Logger", SystemTestLogger::class.java.name)

    // validate RDBRollback alias
    assertEquals(Rollback::class, RDBRollback::class)
    assertEquals("org.springframework.test.annotation.Rollback", RDBRollback::class.java.name)

    // validate TempDirMapping alias
    assertEquals(TempDir::class, TempDirMapping::class)
    assertEquals("org.junit.jupiter.api.io.TempDir", TempDirMapping::class.java.name)
  }

  @Test
  fun rdbRollbackAnnotationShouldWorkCorrectly() {
    val rollbackInstance =
      object {
        @RDBRollback fun testMethod() {}
      }

    val method = rollbackInstance::class.java.getDeclaredMethod("testMethod")
    val annotation = method.getAnnotation(RDBRollback::class.java)
    assertNotNull(annotation)
    assertTrue(annotation.value)
  }

  @Test
  fun tempDirMappingAnnotationShouldWorkCorrectly() {
    val testInstance =
      object {
        fun testMethodWithTempDir(@TempDirMapping tempDir: Path) {}
      }

    val method = testInstance::class.java.getDeclaredMethod("testMethodWithTempDir", Path::class.java)
    val parameters = method.parameters
    assertTrue(parameters.isNotEmpty())
    assertNotNull(parameters[0].getAnnotation(TempDirMapping::class.java))
  }
}
