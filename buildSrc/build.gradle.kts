plugins {
  `kotlin-dsl`
  alias(libs.plugins.org.jetbrains.kotlin.plugin.spring)
}

dependencies {
  implementation(files(libs.javaClass.superclass.protectionDomain.codeSource.location))

  api(libs.io.spring.gradle.dependencyManagementPlugin)
  api(libs.com.diffplug.spotless.comDiffplugSpotlessGradlePlugin)
  api(libs.org.jetbrains.kotlin.jvm.orgJetbrainsKotlinJvmGradlePlugin)
  api(libs.org.jetbrains.kotlin.kapt.orgJetbrainsKotlinKaptGradlePlugin)
  api(libs.org.jetbrains.kotlin.plugin.spring.orgJetbrainsKotlinPluginSpringGradlePlugin)
}



