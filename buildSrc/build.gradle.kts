plugins {
  `kotlin-dsl`
}

val publicRepo = "https://maven.aliyun.com/repository/public"
val gradlePluginRepo = "https://maven.aliyun.com/repository/gradle-plugin"
val springRepo = "https://repo.spring.io/milestone"

repositories {
  mavenLocal()
  maven(publicRepo)
  maven(gradlePluginRepo)
  maven(springRepo)
  gradlePluginPortal()
}

dependencies {
  implementation(files(libs.javaClass.superclass.protectionDomain.codeSource.location))

  api(libs.io.spring.gradle.dependencyManagementPlugin)
  api(libs.com.diffplug.spotless.comDiffplugSpotlessGradlePlugin)
  api(libs.org.jetbrains.kotlin.jvm.orgJetbrainsKotlinJvmGradlePlugin)
  api(libs.org.jetbrains.kotlin.kapt.orgJetbrainsKotlinKaptGradlePlugin)
  api(libs.org.jetbrains.kotlin.plugin.spring.orgJetbrainsKotlinPluginSpringGradlePlugin)
}
