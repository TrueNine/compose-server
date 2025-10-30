plugins {
  `java-gradle-plugin`
  id("buildlogic.kotlin-conventions")
  id("buildlogic.publish-conventions")
  id("buildlogic.spotless-conventions")
}

description =
  """
  Gradle plugin for loading environment variables from .env files.
  Provides dotenv file support for development and testing workflows.
  """
    .trimIndent()

dependencies {
  compileOnly(gradleApi())
  compileOnly(libs.org.jetbrains.kotlin.kotlin.gradle.plugin.api)
  compileOnly(libs.org.jetbrains.kotlin.kotlin.gradle.plugin)
  compileOnly(libs.org.jetbrains.kotlin.kotlin.stdlib)
  compileOnly(gradleKotlinDsl())

  testImplementation(gradleApi())
  testImplementation(gradleTestKit())
  testImplementation(gradleKotlinDsl())
  testImplementation(libs.org.jetbrains.kotlin.kotlin.test.junit5)
  testImplementation(libs.org.junit.platform.junit.platform.suite)
}

tasks.withType<Test>().configureEach {
  jvmArgs =
    listOf(
      "--add-opens",
      "java.base/java.lang=ALL-UNNAMED",
      "--add-opens",
      "java.base/java.util=ALL-UNNAMED",
      "--add-opens",
      "java.base/java.lang.invoke=ALL-UNNAMED",
    )
}

gradlePlugin {
  plugins {
    register("composeserver-dotenv-plugin") {
      id = "io.github.truenine.composeserver.dotenv"
      displayName = "Compose Server Dotenv Plugin"
      implementationClass = "io.github.truenine.composeserver.gradleplugin.dotenv.Main"
      description = "gradle plugin for loading environment variables from .env files"
    }
  }
}

