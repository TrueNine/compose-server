plugins {
  id("buildlogic.kotlinspring-conventions")
  id("buildlogic.spotless-conventions")
}

description = """
Jackson JSON processing library integration with comprehensive data type support.
Provides Kotlin module, JSR310 datetime, JDK8 Optional, Guava, and Joda-Time serialization support.
""".trimIndent()

dependencies {
  api(libs.com.fasterxml.jackson.core.jackson.databind) {
    exclude(
      group = libs.com.google.guava.guava.jre.get().module.group,
      module = libs.com.google.guava.guava.jre.get().module.name,
    )
  }
  implementation(libs.com.google.guava.guava.jre)
  api(libs.com.fasterxml.jackson.module.jackson.module.kotlin)

  implementation(libs.org.springframework.spring.web)

  implementation(libs.com.fasterxml.jackson.datatype.jackson.datatype.jsr310)
  implementation(libs.com.fasterxml.jackson.datatype.jackson.datatype.jdk8)
  implementation(libs.com.fasterxml.jackson.datatype.jackson.datatype.guava)
  implementation(libs.com.fasterxml.jackson.datatype.jackson.datatype.joda)

  api(projects.shared)

  testImplementation(projects.testtoolkit)
  testImplementation(libs.org.springframework.boot.spring.boot.starter.json)
  testImplementation(libs.org.springframework.boot.spring.boot.starter.web)
}
