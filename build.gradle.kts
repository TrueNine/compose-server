plugins {
  idea
  base
  id("buildlogic.repositories-conventions")
  id("buildlogic.spotless-conventions")
  id("buildlogic.jacoco-aggregate")
  alias(libs.plugins.org.jetbrains.kotlin.jvm) apply false
}

idea {
  module {
    isDownloadSources = true
    isDownloadJavadoc = true
  }
}
