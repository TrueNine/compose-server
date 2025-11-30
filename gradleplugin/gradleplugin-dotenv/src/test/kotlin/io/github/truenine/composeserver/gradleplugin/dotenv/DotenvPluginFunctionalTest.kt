package io.github.truenine.composeserver.gradleplugin.dotenv

import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File
import java.nio.file.Path
import kotlin.test.assertEquals

class DotenvPluginFunctionalTest {

  @field:TempDir
  lateinit var tempDir: Path

  @Test
  fun loadsEnvironmentVariablesIntoTestTask() {
    val projectDir = tempDir.resolve("envProject").toFile().apply { mkdirs() }

    writeSettingsFile(projectDir)
    writeEnvFile(projectDir, "FOO=bar")
    writeBuildFile(
      projectDir,
      """
      import org.gradle.api.GradleException
      import org.gradle.api.tasks.testing.Test

      plugins {
        id("java")
        id("io.github.truenine.composeserver.dotenv")
      }

      tasks.register("verifyDotenv") {
        doLast {
          val envMap = tasks.named<Test>("test").get().environment
          if (envMap["FOO"] != "bar") {
            throw GradleException("Expected FOO=bar but was ${'$'}{envMap["FOO"]}")
          }
        }
      }
      """
        .trimIndent()
    )

    val result =
      GradleRunner.create()
        .withProjectDir(projectDir)
        .withPluginClasspath()
        .withArguments("verifyDotenv")
        .build()

    val outcome = result.task(":verifyDotenv")?.outcome
    assertEquals(TaskOutcome.SUCCESS, outcome)
  }

  @Test
  fun handlesMissingEnvFileGracefully() {
    val projectDir = tempDir.resolve("noEnvProject").toFile().apply { mkdirs() }

    writeSettingsFile(projectDir)
    writeBuildFile(
      projectDir,
      """
      import org.gradle.api.GradleException
      import org.gradle.api.tasks.testing.Test

      plugins {
        id("java")
        id("io.github.truenine.composeserver.dotenv")
      }

      tasks.register("verifyDotenvMissing") {
        doLast {
          val envMap = tasks.named<Test>("test").get().environment
          if (envMap.containsKey("FOO")) {
            throw GradleException("Did not expect FOO to be defined when .env is missing")
          }
        }
      }
      """
        .trimIndent()
    )

    val result =
      GradleRunner.create()
        .withProjectDir(projectDir)
        .withPluginClasspath()
        .withArguments("verifyDotenvMissing")
        .build()

    val outcome = result.task(":verifyDotenvMissing")?.outcome
    assertEquals(TaskOutcome.SUCCESS, outcome)
  }

  private fun writeEnvFile(projectDir: File, content: String) {
    projectDir.resolve(".env").writeText(content)
  }

  private fun writeSettingsFile(projectDir: File) {
    projectDir.resolve("settings.gradle.kts").writeText("rootProject.name = \"test-project\"")
  }

  private fun writeBuildFile(projectDir: File, content: String) {
    projectDir.resolve("build.gradle.kts").writeText(content)
  }
}


