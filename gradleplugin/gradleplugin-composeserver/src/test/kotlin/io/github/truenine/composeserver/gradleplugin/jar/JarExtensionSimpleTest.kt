package io.github.truenine.composeserver.gradleplugin.jar

import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.tasks.Copy
import org.gradle.api.tasks.Delete
import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import org.springframework.boot.gradle.plugin.SpringBootPlugin
import java.io.File
import kotlin.test.*

class JarExtensionSimpleTest {

  private lateinit var project: Project
  private lateinit var config: JarExtensionConfig

  @TempDir private lateinit var tempDir: File

  @BeforeEach
  fun setup() {
    project = ProjectBuilder.builder().withProjectDir(tempDir).build()

    // Apply required plugins
    project.pluginManager.apply(JavaPlugin::class.java)
    project.pluginManager.apply(SpringBootPlugin::class.java)

    config = JarExtensionConfig(project)
  }

  @Test
  fun `create_boot_jar_tasks_when_bootJarSeparate_enabled`() {
    config.bootJarSeparate = true

    JarExtension(project, config)

    assertTrue(project.tasks.names.contains(JarExtension.BOOT_JAR_CLEAN_TASK_NAME))
    assertTrue(project.tasks.names.contains(JarExtension.BOOT_JAR_COPY_LIB_TASK_NAME))
    assertTrue(project.tasks.names.contains(JarExtension.BOOT_JAR_COPY_CONFIG_TASK_NAME))
  }

  @Test
  fun `configure_clean_task_with_correct_group`() {
    config.bootJarSeparate = true

    JarExtension(project, config)

    val cleanTask = project.tasks.getByName(JarExtension.BOOT_JAR_CLEAN_TASK_NAME) as Delete

    assertNotNull(cleanTask)
    assertEquals("compose gradle", cleanTask.group)
  }

  @Test
  fun `configure_copy_lib_task_with_correct_group_and_destination`() {
    config.bootJarSeparate = true

    JarExtension(project, config)

    val copyLibTask = project.tasks.getByName(JarExtension.BOOT_JAR_COPY_LIB_TASK_NAME) as Copy

    assertNotNull(copyLibTask)
    assertEquals("compose gradle", copyLibTask.group)
    assertTrue(copyLibTask.destinationDir.absolutePath.contains(config.jarDistDir))
  }

  @Test
  fun `handle_developmentOnly_configuration_safely`() {
    config.bootJarSeparate = true

    // Create a mock developmentOnly configuration if it does not exist
    if (project.configurations.findByName("developmentOnly") == null) {
      project.configurations.create("developmentOnly") { devConfig ->
        devConfig.isCanBeResolved = true
        devConfig.isCanBeConsumed = false
      }
    }

    // Should not throw any exception
    JarExtension(project, config)

    val copyLibTask = project.tasks.getByName(JarExtension.BOOT_JAR_COPY_LIB_TASK_NAME) as Copy
    assertNotNull(copyLibTask)
  }

  @Test
  fun `work_when_developmentOnly_configuration_missing`() {
    config.bootJarSeparate = true

    // Do not create developmentOnly configuration; should still work correctly
    JarExtension(project, config)

    val copyLibTask = project.tasks.getByName(JarExtension.BOOT_JAR_COPY_LIB_TASK_NAME) as Copy
    assertNotNull(copyLibTask)
  }

  @Test
  fun `have_correct_task_name_constants`() {
    assertEquals("composeBootJarClearLib", JarExtension.BOOT_JAR_CLEAN_TASK_NAME)
    assertEquals("composeBootJarCopyLib", JarExtension.BOOT_JAR_COPY_LIB_TASK_NAME)
    assertEquals("composeBootJarCopyConfig", JarExtension.BOOT_JAR_COPY_CONFIG_TASK_NAME)
  }
}
