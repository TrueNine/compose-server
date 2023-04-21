project.version = V.Component.core

dependencies {
  api("com.fasterxml.jackson.core:jackson-annotations")
  api("com.fasterxml.jackson.module:jackson-module-kotlin")
  api("ognl:ognl:${V.Util.ognl}")
  api("com.google.guava:guava:${V.Util.guava}")
  api("jakarta.servlet:jakarta.servlet-api")
  api("io.swagger.core.v3:swagger-annotations-jakarta:${V.StandardEdition.swaggerAnnotationJakarta}")
  api("org.slf4j:slf4j-api")
  api("jakarta.validation:jakarta.validation-api")
  api("org.springframework:spring-webmvc")
  implementation("com.google.code.gson:gson:${V.Util.gson}")
  implementation("org.springframework.boot:spring-boot-starter-json")
  implementation("org.springframework.security:spring-security-crypto")
  implementation("org.bouncycastle:bcprov-jdk15to18:${V.Security.bcprovJdk15to18}")
  implementation("org.springframework:spring-webmvc")
}

tasks.withType<Test> {
  useTestNG {
    suiteXmlFiles.add(File("src/test/resources/testng.xml"))
  }
}
