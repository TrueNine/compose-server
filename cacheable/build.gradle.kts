plugins {
  `kotlinspring-convention`
}

version = libs.versions.composeCacheable.get()

dependencies {
  implementation(libs.bundles.redis)
  implementation(libs.com.github.benManes.caffeine.caffeine)

  implementation(projects.depend.dependJackson)
  implementation(projects.core)
  testImplementation(projects.testToolkit)
}
