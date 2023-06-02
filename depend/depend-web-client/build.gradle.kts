project.version = V.Compose.dependWebClient

dependencies {
  implementation(project(":core"))
  api("org.springframework.boot:spring-boot-starter-webflux")
}
