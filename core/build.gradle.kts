project.version = V.Project.core

dependencies {
  api("org.springframework.security:spring-security-crypto")
  api("org.bouncycastle:bcprov-jdk15to18:${V.Security.bouncyCastle15to18}")
  api("org.springframework.boot:spring-boot-starter-json")
  api("com.fasterxml.jackson.module:jackson-module-kotlin")
  api("jakarta.servlet:jakarta.servlet-api:${V.Api.jakartaServlet}")
  api("ognl:ognl:${V.Util.ognl}")
  api("io.swagger.core.v3:swagger-annotations-jakarta:${V.OpenApi.swaggerAnnotation}")
  api("org.slf4j:slf4j-api")
  api("org.springframework:spring-webmvc")
  api("com.google.guava:guava:${V.Util.guava}")
  api("com.google.code.gson:gson:${V.Util.gson}")
  api("com.fasterxml.jackson.core:jackson-annotations")
}
