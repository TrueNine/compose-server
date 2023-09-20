version = libs.versions.compose.cacheable.get()

dependencies {
  api("org.apache.commons:commons-pool2")
  api("org.springframework.boot:spring-boot-starter-data-redis")
  implementation(project(":core"))
}
