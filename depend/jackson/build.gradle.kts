plugins {
  `kotlinspring-convention`
}

version = libs.versions.composeDependJackson.get()

dependencies {
  api(libs.com.fasterxml.jackson.core.jacksonDatabind)
  api(libs.com.fasterxml.jackson.module.jacksonModuleKotlin)

  implementation(libs.org.springframework.springWeb)

  implementation(libs.com.fasterxml.jackson.datatype.jacksonDatatypeJsr310)
  implementation(libs.com.fasterxml.jackson.datatype.jacksonDatatypeJdk8)
  implementation(libs.com.fasterxml.jackson.datatype.jacksonDatatypeGuava)
  implementation(libs.com.fasterxml.jackson.datatype.jacksonDatatypeJoda)

  implementation(projects.core)

  testImplementation(projects.testToolkit)
  testImplementation(libs.org.springframework.boot.springBootStarterJson)
  testImplementation(libs.org.springframework.boot.springBootStarterWeb)
}
