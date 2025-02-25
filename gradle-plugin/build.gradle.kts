plugins {
  `java-gradle-plugin`
  `kotlin-convention`
  `publish-convention`
}

version = libs.versions.compose.gradle.plugin.get()

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
}

gradlePlugin {
  plugins {
    register("${libs.versions.compose.group.get()}.${project.name}") {
      id = "${libs.versions.compose.group.get()}.${project.name}"
      displayName =
        "${libs.versions.compose.group.get()}.${project.name}.gradle.plugin"
      implementationClass =
        "${libs.versions.compose.group.get()}.gradleplugin.Main"
      description = "compose server development gradle plugin"
    }

    register("${libs.versions.compose.group.get()}.settings-${project.name}") {
      id = "${libs.versions.compose.group.get()}.settings-${project.name}"
      displayName =
        "${libs.versions.compose.group.get()}.settings-${project.name}.gradle.plugin"
      implementationClass =
        "${libs.versions.compose.group.get()}.gradleplugin.SettingsMain"
      description = "compose server development gradle settings plugin"
    }
  }
}
