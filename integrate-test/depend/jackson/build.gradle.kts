plugins {
  id("buildlogic.kotlinspring-conventions")
  id("buildlogic.spotless-conventions")
}

dependencies {
  implementation(projects.depend.dependJackson)
  testImplementation(projects.testtoolkit)
  testImplementation(libs.org.springframework.boot.spring.boot.starter.web)
  testImplementation(libs.org.springframework.boot.spring.boot.starter.test)
}
