project.version = V.Component.webApiDoc

dependencies {
  api("com.github.xiaoymin:knife4j-springdoc-ui:${V.Web.knife4j}")
  implementation("org.springframework.boot:spring-boot-starter-web")
  api("org.springdoc:springdoc-openapi-starter-webmvc-ui:${V.Web.springdocOpenapiWebmvcUi}")
  implementation(project(":core"))
}
