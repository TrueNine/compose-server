import net.yan100.compose.plugin.V

version = libs.versions.compose.core.get()

dependencies {
  api("com.fasterxml.jackson.core:jackson-annotations")
  api("com.fasterxml.jackson.module:jackson-module-kotlin")

  api("ognl:ognl:${V.Util.ognl}")
  api(libs.util.guava.jre)
  api("jakarta.servlet:jakarta.servlet-api")
  api("jakarta.validation:jakarta.validation-api")
  api("io.swagger.core.v3:swagger-annotations-jakarta:${V.StandardEdition.swaggerAnnotationJakarta}")
  api("org.slf4j:slf4j-api")
  api("org.springframework.modulith:spring-modulith-starter-core:${V.Spring.modulith}")

  implementation("org.springframework.boot:spring-boot-starter-json")
  implementation("org.springframework.security:spring-security-crypto")
  implementation("org.bouncycastle:bcprov-jdk18on:${V.Security.bcprovJdk18on}")
  implementation("org.springframework:spring-webmvc")

  // TODO 日志
  implementation("org.springframework.boot:spring-boot-starter-logging")

  // hutool
  implementation("cn.hutool:hutool-core:${V.Util.huTool}")
  implementation("cn.hutool:hutool-crypto:${V.Util.huTool}")
}

tasks.withType<Test> {
  useTestNG {
    suiteXmlFiles.add(File("src/test/resources/testng.xml"))
  }
}
