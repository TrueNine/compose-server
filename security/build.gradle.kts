project.version = V.Project.security


dependencies {
  implementation("org.owasp.antisamy:antisamy:${V.Security.antisamy}") {
    exclude(group = "net.sourceforge.nekohtml", module = "nekohtml")
  }
  implementation("net.sourceforge.nekohtml:nekohtml:${V.Security.nekohtml}")

  implementation(project(":core"))
  api("org.springframework.boot:spring-boot-starter-security")
  api("cn.hutool:hutool-captcha:${V.Util.huTool}")
  api("com.auth0:java-jwt:${V.Jwt.auth0Jwt}")

  testImplementation("org.springframework.security:spring-security-test")
}
