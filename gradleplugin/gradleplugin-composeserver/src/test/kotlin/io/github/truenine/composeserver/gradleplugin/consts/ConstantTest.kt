package io.github.truenine.composeserver.gradleplugin.consts

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ConstantTest {

  @Test
  fun `have_correct_task_group`() {
    assertEquals("compose gradle", Constant.TASK_GROUP)
  }

  @Test
  fun `have_correct_internal_constants`() {
    assertEquals("meta.init.gradle.kts", Constant.Internal.META_INIT_GRADLE_KTS)
    assertEquals("init.gradle.kts", Constant.Internal.INIT_GRADLE_KTS)
  }

  @Test
  fun `have_correct_file_name_constants`() {
    assertEquals("LICENSE", Constant.FileName.LICENSE)
  }

  @Test
  fun `have_correct_file_name_sets`() {
    val licenseSet = Constant.FileNameSet.LICENSE
    assertTrue(licenseSet.contains("license"))
    assertTrue(licenseSet.contains("license.txt"))
    assertTrue(licenseSet.contains("license")) // from FileName.LICENSE.lowercase()
    assertEquals(3, licenseSet.size)
  }

  @Test
  fun `have_correct_gradle_constants`() {
    assertEquals("unspecified", Constant.Gradle.UNKNOWN_PROJECT_VERSION)
  }

  @Test
  fun `file name set should be case insensitive`() {
    val licenseSet = Constant.FileNameSet.LICENSE
    // All values should be lowercase
    licenseSet.forEach { fileName -> assertEquals(fileName.lowercase(), fileName) }
  }
}
