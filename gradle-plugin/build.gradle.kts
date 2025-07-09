plugins {
  `java-gradle-plugin`
  id("buildlogic.kotlin-conventions")
  id("buildlogic.publish-conventions")
}

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
    register("${libs.versions.group.get()}.${project.name}") {
      id = "${libs.versions.group.get()}.${project.name}"
      displayName = "${libs.versions.group.get()}.${project.name}.gradle.plugin"
      implementationClass = "${libs.versions.group.get()}.gradleplugin.Main"
      description = "compose server development gradle plugin"
    }
  }
}
