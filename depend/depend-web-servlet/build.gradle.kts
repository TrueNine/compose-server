project.version = V.Component.dependWebServlet

dependencies {
  api("org.springframework.boot:spring-boot-starter-web") {
    exclude("org.springframework.boot","spring-boot-starter-tomcat")
  }
  api("org.springframework.boot:spring-boot-starter-undertow")
}
