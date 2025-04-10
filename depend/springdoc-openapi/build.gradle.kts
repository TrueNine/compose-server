plugins { `kotlinspring-convention` }

version = libs.versions.compose.depend.get()

dependencies {
  api(libs.io.swagger.core.v3.swagger.annotations.jakarta)
  api(projects.shared)

  implementation(libs.org.springdoc.springdoc.openapi.starter.webmvc.ui)

  testImplementation(projects.testtoolkit)
  testImplementation(libs.org.springframework.boot.spring.boot.starter.web)
}
