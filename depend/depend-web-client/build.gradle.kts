version = libs.versions.compose.depend.webclient.get()

dependencies {
  implementation(project(":core"))
  api("org.springframework.boot:spring-boot-starter-webflux")
}
