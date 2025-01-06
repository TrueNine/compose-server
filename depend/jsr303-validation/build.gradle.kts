plugins {
  `kotlin-convention`
}

version = libs.versions.composeDependJsr303Validation.get()

dependencies {
  testImplementation(projects.testToolkit)
  testImplementation(projects.rds.rdsCore)

  testImplementation(libs.org.springframework.boot.springBootStarterWeb)
  testImplementation(libs.org.springframework.boot.springBootStarterDataJpa)
  implementation(libs.org.springframework.boot.springBootStarterValidation)

  implementation(projects.core)
}
