project.version = libs.versions.compose.depend.web.client.get()

dependencies {
  implementation(project(":core"))
  api("org.springframework.boot:spring-boot-starter-webflux")
}
