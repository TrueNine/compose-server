plugins {
  `kotlinspring-convention`
  `publish-convention`
}

version = libs.versions.compose.asProvider().get()

dependencies {
  api(libs.com.fasterxml.jackson.core.jackson.annotations)
  api(libs.jakarta.annotation.jakarta.annotation.api)
  api(libs.jakarta.servlet.jakarta.servlet.api)
  api(libs.io.swagger.core.v3.swagger.annotations.jakarta)
  api(libs.org.slf4j.slf4j.api)
  implementation(projects.meta)

  testImplementation(projects.testtoolkit)
  testImplementation(libs.org.springframework.boot.spring.boot.starter.web)
}
