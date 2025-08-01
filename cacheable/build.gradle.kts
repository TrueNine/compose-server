plugins {
  id("buildlogic.kotlinspring-conventions")
  id("buildlogic.spotless-conventions")
}

description =
  """
Caching abstractions and implementations supporting both distributed and local caching strategies.
Includes Redis integration for distributed caching and Caffeine for high-performance local caching.
"""
    .trimIndent()

dependencies {
  implementation(libs.bundles.redis)
  implementation(libs.com.github.ben.manes.caffeine.caffeine)

  implementation(projects.depend.dependJackson)
  api(projects.shared)
  testImplementation(projects.testtoolkit)
}
