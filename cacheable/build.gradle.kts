plugins { `kotlinspring-convention` }

version = libs.versions.compose.asProvider().get()

dependencies {
  implementation(libs.bundles.redis)
  implementation(libs.com.github.ben.manes.caffeine.caffeine)

  implementation(projects.depend.dependJackson)
  implementation(projects.core)
  testImplementation(projects.testtoolkit)
}
