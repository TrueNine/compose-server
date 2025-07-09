plugins {
  id("buildlogic.kotlin-conventions")
}

dependencies {
  implementation(libs.com.fasterxml.jackson.core.jackson.annotations)
  testImplementation(projects.testtoolkit)
}
