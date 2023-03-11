project.version = V.Project.security


dependencies {
  // TODO 修改版本号
  api("org.owasp.antisamy:antisamy:${V.Security.antisamy}")
  implementation(project(":core"))

  api("org.springframework.boot:spring-boot-starter-security")
  api("cn.hutool:hutool-captcha:${V.Util.huTool}")
  api("com.auth0:java-jwt:${V.Jwt.auth0Jwt}")

  testImplementation("org.springframework.security:spring-security-test")
}
