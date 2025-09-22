plugins {
  id("buildlogic.kotlinspring-test-conventions")
  id("buildlogic.spotless-conventions")
  id("buildlogic.loadenv-conventions")
}

dependencies {
  implementation(projects.oss.ossVolcengineTos)

  testImplementation(libs.com.volcengine.ve.tos.java.sdk)

  testImplementation(projects.testtoolkit.testtoolkitSpringmvc)
  testImplementation(projects.testtoolkit.testtoolkitTestcontainers)
  testImplementation(libs.org.springframework.boot.spring.boot.starter.web)
}
