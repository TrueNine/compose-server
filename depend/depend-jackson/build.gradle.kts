plugins {
  id("buildlogic.kotlin-spring-boot-conventions")
  id("buildlogic.spotless-conventions")
}

description =
  """
  Jackson JSON processing library integration with comprehensive data type support.
  Provides Kotlin module, JSR310 datetime, JDK8 Optional, Guava, and Joda-Time serialization support.
  """
    .trimIndent()

dependencies {
  api(libs.tools.jackson.core.jackson.databind) {
    exclude(group = libs.com.google.guava.guava.jre.get().module.group, module = libs.com.google.guava.guava.jre.get().module.name)
  }
  implementation(libs.com.google.guava.guava.jre)
  api(libs.tools.jackson.module.jackson.module.kotlin)

  implementation(libs.org.springframework.spring.web)

  implementation(libs.tools.jackson.datatype.jackson.datatype.guava)

  api(projects.shared)

  testImplementation(projects.testtoolkit.testtoolkitSpringmvc)
  testImplementation(libs.org.springframework.boot.spring.boot.starter.json)
  testImplementation(libs.org.springframework.boot.spring.boot.starter.web)

  kaptTest(libs.com.fasterxml.jackson.core.jackson.annotations)
}

// Disable KAPT for test sources to avoid Jackson 3.x annotation processing issues
afterEvaluate {
  tasks.findByName("kaptTestKotlin")?.enabled = false
  tasks.findByName("kaptGenerateStubsTestKotlin")?.enabled = false
}
