project.version = V.Project.webServlet

dependencies {
  api("org.springframework.security:spring-security-core")
  implementation(project(":core"))
  api("org.springframework.boot:spring-boot-starter-web")
  api("org.springframework.boot:spring-boot-starter-validation")
}
