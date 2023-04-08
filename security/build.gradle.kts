project.version = V.Component.security


dependencies {
  api("org.springframework.boot:spring-boot-starter-security")
  api("cn.hutool:hutool-captcha:${V.Util.huTool}")
  api("com.auth0:java-jwt:${V.Web.auth0JavaJwt}")
  implementation("org.owasp.antisamy:antisamy:${V.Security.antisamy}") {
    exclude(group = "net.sourceforge.nekohtml", module = "nekohtml")
    implementation("net.sourceforge.nekohtml:nekohtml:${V.Security.nekohtml}")
  }
  implementation(project(":core"))
  testImplementation("org.springframework.security:spring-security-test")
}
