package io.github.truenine.composeserver.gradleplugin.consts

import kotlin.test.assertEquals
import kotlin.test.assertTrue
import org.junit.jupiter.api.Test

class ConstantTest {

  @Test
  fun `should have correct task group`() {
    assertEquals("compose gradle", Constant.TASK_GROUP)
  }

  @Test
  fun `should have correct internal constants`() {
    assertEquals("meta.init.gradle.kts", Constant.Internal.META_INIT_GRADLE_KTS)
    assertEquals("init.gradle.kts", Constant.Internal.INIT_GRADLE_KTS)
  }

  @Test
  fun `should have correct file name constants`() {
    assertEquals("LICENSE", Constant.FileName.LICENSE)
  }

  @Test
  fun `should have correct file name sets`() {
    val licenseSet = Constant.FileNameSet.LICENSE
    assertTrue(licenseSet.contains("license"))
    assertTrue(licenseSet.contains("license.txt"))
    assertTrue(licenseSet.contains("license")) // from FileName.LICENSE.lowercase()
    assertEquals(3, licenseSet.size)
  }

  @Test
  fun `should have correct gradle constants`() {
    assertEquals("unspecified", Constant.Gradle.UNKNOWN_PROJECT_VERSION)
  }

  @Test
  fun `file name set should be case insensitive`() {
    val licenseSet = Constant.FileNameSet.LICENSE
    // All values should be lowercase
    licenseSet.forEach { fileName -> assertEquals(fileName.lowercase(), fileName) }
  }
}
