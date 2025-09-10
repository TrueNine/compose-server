plugins {
  idea
  base
  id("buildlogic.repositories-conventions")
  id("buildlogic.spotless-conventions")
  alias(libs.plugins.org.jetbrains.kotlin.jvm) apply false
}

idea { module { isDownloadSources = true } }
