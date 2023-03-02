project.version = V.Project.webApiDoc

dependencies {
  api("com.github.xiaoymin:knife4j-springdoc-ui:${V.OpenApi.knife4j}")
  implementation("org.springframework.boot:spring-boot-starter-web")
  api("org.springdoc:springdoc-openapi-starter-webmvc-ui:${V.OpenApi.springDoc2}")
  implementation("${group}:core:${V.Project.core}")
}
