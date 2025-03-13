plugins { `kotlinspring-convention` }

version = libs.versions.compose.oss.get()

dependencies {
  implementation(
    libs.com.fasterxml.jackson.core.jackson.databind
  )
  implementation(projects.core)
  testImplementation(projects.testtoolkit)
}
