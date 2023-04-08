project.version = V.Component.webApiDoc

dependencies {
  api("com.github.xiaoymin:knife4j-springdoc-ui:${V.Web.knife4j}")
  api("org.springdoc:springdoc-openapi-starter-webmvc-ui:${V.Web.springdocOpenapiWebmvcUi}")
  implementation("org.springframework.boot:spring-boot-starter-web")
  implementation(project(":core"))
}
