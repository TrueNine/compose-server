version = libs.versions.compose.depend.webservlet.get()


dependencies {
  api(libs.spring.boot.validation)
  api(libs.spring.boot.web) {
    exclude("org.springframework.boot", "spring-boot-starter-tomcat")
  }
  api(libs.spring.boot.undertow)
  api(libs.spring.boot.websocket) {
    exclude(group = "org.springframework.boot", module = "spring-boot-starter-tomcat")
  }
  implementation(project(":core"))
}
