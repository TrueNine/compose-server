project.version = V.Compose.dependWebServlet

dependencies {
  implementation(project(":core"))
  api("org.springframework.boot:spring-boot-starter-validation")
  api("org.springframework.boot:spring-boot-starter-web") {
    exclude("org.springframework.boot", "spring-boot-starter-tomcat")
  }
  api("org.springframework.boot:spring-boot-starter-undertow")
}
