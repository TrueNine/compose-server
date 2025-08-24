plugins {
  id("buildlogic.kotlinspring-conventions")
  id("buildlogic.spotless-conventions")
}

dependencies {
  implementation(projects.oss.ossMinio)

  testImplementation(projects.testtoolkit.testtoolkitSpringmvc)
  testImplementation(projects.testtoolkit.testtoolkitTestcontainers)
  testImplementation(libs.org.springframework.boot.spring.boot.starter.web)
}
