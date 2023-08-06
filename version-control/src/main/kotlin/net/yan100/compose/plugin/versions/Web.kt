package net.yan100.compose.plugin.versions

object Web {
  // 用于发送网络请求
  // https://mvnrepository.com/artifact/com.squareup.okhttp3/okhttp
  val okhttp3 = "5.0.0-alpha.11"

  // https://mvnrepository.com/artifact/org.springdoc/springdoc-openapi-starter-webmvc-ui
  const val springdocOpenapiWebmvcUi = "2.1.0"

  // https://mvnrepository.com/artifact/org.springdoc/springdoc-openapi-ui
  const val springdocOpenapiUi = "1.7.0"

  // https://doc.xiaominfo.com/
  // https://mvnrepository.com/artifact/com.github.xiaoymin/knife4j-spring-boot-starter
  // https://mvnrepository.com/artifact/com.github.xiaoymin/knife4j-openapi3-jakarta-spring-boot-starter
  const val knife4j = "3.0.3"
  const val knife4jJakarta = "4.2.0"

  // auth0 JWT
  // https://mvnrepository.com/artifact/com.auth0/java-jwt
  const val auth0JavaJwt = "4.4.0"

  // https://mvnrepository.com/artifact/io.jsonwebtoken/jjwt
  const val jJwt = "0.9.1"
}
