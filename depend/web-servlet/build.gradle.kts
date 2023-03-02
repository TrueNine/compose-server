project.version = V.Project.webServlet

dependencies {
  api("org.springframework.security:spring-security-core")
  api("$group:core:${V.Project.core}")
  api("org.springframework.boot:spring-boot-starter-web")
  api("org.springframework.boot:spring-boot-starter-validation")
}
