plugins { `kotlinspring-convention` }

version = libs.versions.compose.depend.jackson.get()

dependencies {
  api(libs.com.fasterxml.jackson.core.jackson.databind)
  api(libs.com.fasterxml.jackson.module.jackson.module.kotlin)

  implementation(libs.org.springframework.spring.web)

  implementation(libs.com.fasterxml.jackson.datatype.jackson.datatype.jsr310)
  implementation(libs.com.fasterxml.jackson.datatype.jackson.datatype.jdk8)
  implementation(libs.com.fasterxml.jackson.datatype.jackson.datatype.guava)
  implementation(libs.com.fasterxml.jackson.datatype.jackson.datatype.joda)

  implementation(projects.core)

  testImplementation(projects.testtoolkit)
  testImplementation(libs.org.springframework.boot.spring.boot.starter.json)
  testImplementation(libs.org.springframework.boot.spring.boot.starter.web)
}
