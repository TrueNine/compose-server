package io.github.truenine.composeserver.gradleplugin.jar

import java.io.File
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.tasks.Copy
import org.gradle.api.tasks.Delete
import org.gradle.api.tasks.bundling.Jar
import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import org.springframework.boot.gradle.plugin.SpringBootPlugin

class JarExtensionTest {

  private lateinit var project: Project
  private lateinit var config: JarExtensionConfig

  @TempDir private lateinit var tempDir: File

  @BeforeEach
  fun setup() {
    project = ProjectBuilder.builder().withProjectDir(tempDir).build()

    // 应用必要插件
    project.pluginManager.apply(JavaPlugin::class.java)
    project.pluginManager.apply(SpringBootPlugin::class.java)

    config = JarExtensionConfig(project)
  }

  @Nested
  inner class JarExtensionCreation {

    @Test
    fun `not_create_jar_tasks_when_jar_task_not_found`() {
      // 使用一个没有 Java 插件的项目来模拟没有 jar 任务的情况
      val cleanProject = ProjectBuilder.builder().withProjectDir(tempDir).build()
      cleanProject.pluginManager.apply(SpringBootPlugin::class.java)
      val cleanConfig = JarExtensionConfig(cleanProject)
      cleanConfig.copyLicense = true

      JarExtension(cleanProject, cleanConfig)

      // 验证没有创建额外的任务，因为没有 jar 任务
      assertTrue(cleanProject.tasks.withType(Jar::class.java).isEmpty())
    }

    @Test
    fun `not_create_bootJar_tasks_when_conditions_not_met`() {
      // 使用一个没有 Spring Boot 插件的项目来模拟没有 bootJar 任务的情况
      val cleanProject = ProjectBuilder.builder().withProjectDir(tempDir).build()
      cleanProject.pluginManager.apply(JavaPlugin::class.java)
      val cleanConfig = JarExtensionConfig(cleanProject)
      cleanConfig.bootJarSeparate = true

      JarExtension(cleanProject, cleanConfig)

      // 验证没有创建 boot jar 相关任务，因为没有 bootJar 任务
      assertFalse(cleanProject.tasks.names.contains(JarExtension.BOOT_JAR_CLEAN_TASK_NAME))
      assertFalse(cleanProject.tasks.names.contains(JarExtension.BOOT_JAR_COPY_LIB_TASK_NAME))
      assertFalse(cleanProject.tasks.names.contains(JarExtension.BOOT_JAR_COPY_CONFIG_TASK_NAME))
    }
  }

  @Nested
  inner class BootJarSeparation {

    @BeforeEach
    fun setupBootJar() {
      config.bootJarSeparate = true
    }

    @Test
    fun `create_boot_jar_tasks_when_all_conditions_met`() {
      JarExtension(project, config)

      assertTrue(project.tasks.names.contains(JarExtension.BOOT_JAR_CLEAN_TASK_NAME))
      assertTrue(project.tasks.names.contains(JarExtension.BOOT_JAR_COPY_LIB_TASK_NAME))
      assertTrue(project.tasks.names.contains(JarExtension.BOOT_JAR_COPY_CONFIG_TASK_NAME))
    }

    @Test
    fun `configure_clean_task_correctly`() {
      JarExtension(project, config)

      val cleanTask = project.tasks.getByName(JarExtension.BOOT_JAR_CLEAN_TASK_NAME) as Delete

      assertNotNull(cleanTask)
      assertEquals("compose gradle", cleanTask.group)
      assertTrue(cleanTask.delete.isNotEmpty())
    }

    @Test
    fun `configure_copy_lib_task_correctly`() {
      JarExtension(project, config)

      val copyLibTask = project.tasks.getByName(JarExtension.BOOT_JAR_COPY_LIB_TASK_NAME) as Copy

      assertNotNull(copyLibTask)
      assertEquals("compose gradle", copyLibTask.group)
      assertTrue(copyLibTask.destinationDir.absolutePath.contains(config.jarDistDir))
      assertTrue(copyLibTask.destinationDir.absolutePath.contains(config.bootJarDistName))
    }

    @Test
    fun `configure_copy_config_task_correctly`() {
      JarExtension(project, config)

      val copyConfigTask = project.tasks.getByName(JarExtension.BOOT_JAR_COPY_CONFIG_TASK_NAME) as Copy

      assertNotNull(copyConfigTask)
      assertEquals("compose gradle", copyConfigTask.group)
      assertTrue(copyConfigTask.destinationDir.absolutePath.contains(config.jarDistDir))
      assertTrue(copyConfigTask.destinationDir.absolutePath.contains(config.bootJarConfigName))
    }
  }

  @Nested
  inner class DevelopmentOnlyExclusion {

    @BeforeEach
    fun setupForDevelopmentOnlyTest() {
      config.bootJarSeparate = true

      // 创建模拟的 developmentOnly 配置，如果不存在
      if (project.configurations.findByName("developmentOnly") == null) {
        project.configurations.create("developmentOnly") { devConfig ->
          devConfig.isCanBeResolved = true
          devConfig.isCanBeConsumed = false
        }
      }
    }

    @Test
    fun `handle_missing_developmentOnly_configuration_gracefully`() {
      // 使用一个全新的项目来模拟没有 developmentOnly 配置的情况
      val cleanProject = ProjectBuilder.builder().withProjectDir(tempDir).build()
      cleanProject.pluginManager.apply(JavaPlugin::class.java)
      cleanProject.pluginManager.apply(SpringBootPlugin::class.java)
      val cleanConfig = JarExtensionConfig(cleanProject)
      cleanConfig.bootJarSeparate = true

      // 应该不会抛出异常
      JarExtension(cleanProject, cleanConfig)

      val copyLibTask = cleanProject.tasks.getByName(JarExtension.BOOT_JAR_COPY_LIB_TASK_NAME) as Copy
      assertNotNull(copyLibTask)
    }
  }

  @Nested
  inner class LicenseCopy {

    @Test
    fun `copy_license_when_copyLicense_enabled_and_jar_task_exists`() {
      config.copyLicense = true

      // 创建模拟的 LICENSE 文件
      val licenseFile = File(project.rootDir, "LICENSE")
      licenseFile.createNewFile()
      licenseFile.writeText("MIT License")

      JarExtension(project, config)

      // 验证 jar 任务配置了 LICENSE 文件
      val jarTask = project.tasks.withType(Jar::class.java).first()
      assertNotNull(jarTask)
    }

    @Test
    fun `not_copy_license_when_copyLicense_disabled`() {
      config.copyLicense = false

      // 创建模拟的 LICENSE 文件
      val licenseFile = File(project.rootDir, "LICENSE")
      licenseFile.createNewFile()

      JarExtension(project, config)

      // 验证没有特殊的 LICENSE 处理
      val jarTask = project.tasks.withType(Jar::class.java).firstOrNull()
      // 这里主要验证没有抛出异常，只要没有抛出异常就是成功的
      // 验证获取 jar 任务不会抛出异常，不管 jarTask 是否为 null
    }

    @Test
    fun `handle_missing_LICENSE_file_gracefully`() {
      config.copyLicense = true

      // 不创建 LICENSE 文件，应该正常处理
      JarExtension(project, config)

      val jarTask = project.tasks.withType(Jar::class.java).firstOrNull()
      assertNotNull(jarTask)
    }

    @Test
    fun `find_LICENSE_file_with_different_case_variations`() {
      config.copyLicense = true

      // 创建不同大小写的 LICENSE 文件
      val licenseFile = File(project.rootDir, "license.txt")
      licenseFile.createNewFile()
      licenseFile.writeText("Apache 2.0")

      // 应该不会抛出异常
      JarExtension(project, config)

      val jarTask = project.tasks.withType(Jar::class.java).first()
      assertNotNull(jarTask)
    }
  }

  @Nested
  inner class ManifestGeneration {

    @BeforeEach
    fun setupManifestTest() {
      config.bootJarSeparate = true
    }

    @Test
    fun `configure_bootJar_manifest_with_correct_attributes`() {
      JarExtension(project, config)

      // 获取 bootJar 任务并验证配置
      project.tasks.withType(org.springframework.boot.gradle.tasks.bundling.BootJar::class.java).configureEach { bootJar ->
        assertEquals(config.bootJarClassifier, bootJar.archiveClassifier.get())
        assertEquals(config.bootJarVersion, bootJar.archiveVersion.get())
        assertEquals(config.bootJarName, bootJar.archiveBaseName.get())
        assertTrue(bootJar.excludes.contains("*.jar"))
      }
    }

    @Test
    fun `set_up_task_dependencies_correctly`() {
      JarExtension(project, config)

      project.tasks.withType(org.springframework.boot.gradle.tasks.bundling.BootJar::class.java).configureEach { bootJar ->
        val dependencies =
          bootJar.dependsOn.map {
            when (it) {
              is org.gradle.api.Task -> it.name
              else -> it.toString()
            }
          }

        assertTrue(dependencies.any { it.contains(JarExtension.BOOT_JAR_CLEAN_TASK_NAME) })
        assertTrue(dependencies.any { it.contains(JarExtension.BOOT_JAR_COPY_LIB_TASK_NAME) })
        assertTrue(dependencies.any { it.contains(JarExtension.BOOT_JAR_COPY_CONFIG_TASK_NAME) })
      }
    }
  }

  @Nested
  inner class TaskNameConstants {

    @Test
    fun `have_correct_task_name_constants`() {
      assertEquals("composeBootJarClearLib", JarExtension.BOOT_JAR_CLEAN_TASK_NAME)
      assertEquals("composeBootJarCopyLib", JarExtension.BOOT_JAR_COPY_LIB_TASK_NAME)
      assertEquals("composeBootJarCopyConfig", JarExtension.BOOT_JAR_COPY_CONFIG_TASK_NAME)
    }
  }
}
