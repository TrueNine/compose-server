import net.yan100.compose.plugin.V
project.version = V.Compose.cacheable

dependencies {
  api("org.apache.commons:commons-pool2")
  api("org.springframework.boot:spring-boot-starter-data-redis")
  implementation(project(":core"))
}
