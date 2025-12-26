plugins {
  `java-gradle-plugin`
  id("buildlogic.kotlin-conventions")
  id("buildlogic.maven-publish-conventions")
  id("buildlogic.spotless-conventions")
}

description =
  """
  Custom Gradle plugin for Compose Server development workflow automation.
  Provides project configuration, build conventions, and Spring Boot integration for development efficiency.
  """
    .trimIndent()

dependencies {
  compileOnly(gradleApi())
  compileOnly(libs.org.jetbrains.kotlin.kotlin.gradle.plugin.api)
  compileOnly(libs.org.jetbrains.kotlin.kotlin.gradle.plugin)
  compileOnly(libs.org.jetbrains.kotlin.kotlin.stdlib)
  compileOnly(gradleKotlinDsl())

  implementation(libs.org.springframework.boot.spring.boot.gradle.plugin)

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
    register("composeserver-gradle-plugin") {
      id = "io.github.truenine.composeserver.composeserver"
      displayName = "Compose Server Gradle Plugin"
      implementationClass = "io.github.truenine.composeserver.gradleplugin.Main"
      description = "compose server development gradle plugin"
    }
  }
}
