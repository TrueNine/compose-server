plugins {
  id("buildlogic.kotlinspring-conventions")
  id("buildlogic.spotless-conventions")
}

dependencies {
  implementation(projects.depend.dependJackson)

  testImplementation(projects.testtoolkit.testtoolkitSpringmvc)
  testImplementation(libs.org.springframework.boot.spring.boot.starter.web)
}
