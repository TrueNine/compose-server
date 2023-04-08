project.version = V.Component.security


dependencies {
  implementation("org.owasp.antisamy:antisamy") {
    exclude(group = "net.sourceforge.nekohtml", module = "nekohtml")
    implementation("net.sourceforge.nekohtml:nekohtml")
  }

  implementation(project(":core"))
  api("org.springframework.boot:spring-boot-starter-security")
  api("cn.hutool:hutool-captcha")
  api("com.auth0:java-jwt")
  testImplementation("org.springframework.security:spring-security-test")
}
