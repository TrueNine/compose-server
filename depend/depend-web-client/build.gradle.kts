import net.yan100.compose.plugin.V

project.version = V.Compose.DEPEND_WEB_CLIENT

dependencies {
  implementation(project(":core"))
  api("org.springframework.boot:spring-boot-starter-webflux")
}
