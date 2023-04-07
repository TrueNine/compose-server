project.version = V.Project.dependWebServlet

dependencies {
  api("org.springframework.boot:spring-boot-starter-validation")
  api("org.springframework.security:spring-security-core")
  api("org.springframework.boot:spring-boot-starter-web")
  api("org.springframework.boot:spring-boot-starter-validation")
  implementation(project(":core"))
}
