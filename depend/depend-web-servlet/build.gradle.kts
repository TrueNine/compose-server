import net.yan100.compose.plugin.V

project.version = V.Compose.DEPEND_WEB_SERVLET

dependencies {
  implementation(project(":core"))
  api("org.springframework.boot:spring-boot-starter-validation")
  api("org.springframework.boot:spring-boot-starter-web") {
    exclude("org.springframework.boot", "spring-boot-starter-tomcat")
  }
  api("org.springframework.boot:spring-boot-starter-undertow")
  api("org.springframework.boot:spring-boot-starter-websocket") {
    exclude(group = "org.springframework.boot",module = "spring-boot-starter-tomcat")
  }
}
