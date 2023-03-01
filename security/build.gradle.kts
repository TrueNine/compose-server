dependencies {
  // TODO 修改版本号
  api("org.owasp.antisamy:antisamy:${V.Security.antisamy}")
  api(V.Component.pkgV("core"))

  testApi("org.springframework.security:spring-security-test")

  api("org.springframework.boot:spring-boot-starter-security")
  api("cn.hutool:hutool-captcha:${V.Util.huTool}")
  api("com.auth0:java-jwt:${V.Jwt.auth0Jwt}")
}
