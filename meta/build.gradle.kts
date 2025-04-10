plugins { `kotlin-convention` }

version = libs.versions.compose.asProvider().get()

dependencies {
  implementation(libs.com.fasterxml.jackson.core.jackson.annotations)

  testImplementation(projects.shared)
  testImplementation(projects.testtoolkit)
}
