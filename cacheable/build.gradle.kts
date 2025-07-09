plugins {
  id("buildlogic.kotlinspring-conventions")
}

dependencies {
  implementation(libs.bundles.redis)
  implementation(libs.com.github.ben.manes.caffeine.caffeine)

  implementation(projects.depend.dependJackson)
  api(projects.shared)
  testImplementation(projects.testtoolkit)
}
