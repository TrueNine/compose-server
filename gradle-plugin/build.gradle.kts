plugins {
  `java-gradle-plugin`
  `kotlin-convention`
  `publish-convention`
}

version = libs.versions.composeGradlePlugin.get()

dependencies {
  compileOnly(gradleApi())
  compileOnly(libs.org.jetbrains.kotlin.kotlinGradlePluginApi)
  compileOnly(libs.org.jetbrains.kotlin.kotlinGradlePlugin)
  compileOnly(libs.org.jetbrains.kotlin.kotlinStdlib)
  compileOnly(gradleKotlinDsl())

  implementation(libs.org.springframework.boot.springBootGradlePlugin)

  testImplementation(gradleApi())
  testImplementation(gradleTestKit())
  testImplementation(gradleKotlinDsl())
}

gradlePlugin {
  plugins {
    register("${libs.versions.composeGroup.get()}.${project.name}") {
      id = "${libs.versions.composeGroup.get()}.${project.name}"
      displayName = "${libs.versions.composeGroup.get()}.${project.name}.gradle.plugin"
      implementationClass = "${libs.versions.composeGroup.get()}.gradleplugin.Main"
      description = "compose server development gradle plugin"
    }

    register("${libs.versions.composeGroup.get()}.settings-${project.name}") {
      id = "${libs.versions.composeGroup.get()}.settings-${project.name}"
      displayName = "${libs.versions.composeGroup.get()}.settings-${project.name}.gradle.plugin"
      implementationClass = "${libs.versions.composeGroup.get()}.gradleplugin.SettingsMain"
      description = "compose server development gradle settings plugin"
    }
  }
}
