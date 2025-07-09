plugins {
  id("buildlogic.kotlinspring-conventions")
}

version = libs.versions.compose.depend.get()

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
