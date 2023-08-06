import net.yan100.compose.plugin.V

project.version = V.Compose.WEB_API_DOC

dependencies {
  implementation("com.github.xiaoymin:knife4j-openapi3-jakarta-spring-boot-starter:${V.Web.knife4jJakarta}")
  implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:${V.Web.springdocOpenapiWebmvcUi}")
  implementation(project(":core"))
}
