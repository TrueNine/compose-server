project.version = V.Component.webApiDoc

dependencies {
  api("com.github.xiaoymin:knife4j-springdoc-ui")
  api("org.springdoc:springdoc-openapi-starter-webmvc-ui")
  implementation("org.springframework.boot:spring-boot-starter-web")
  implementation(project(":core"))
}
