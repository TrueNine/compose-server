project.version = V.Component.webApiDoc

dependencies {
  implementation("com.github.xiaoymin:knife4j-openapi3-jakarta-spring-boot-starter:${V.Web.knife4jJakarta}")
  implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:${V.Web.springdocOpenapiWebmvcUi}")
  implementation(project(":core"))
}
