project.version = V.Component.core

dependencies {
  api("org.springframework.security:spring-security-crypto")
  api("org.bouncycastle:bcprov-jdk15to18:${V.Security.bcprovJdk15to18}")
  api("org.springframework.boot:spring-boot-starter-json")
  api("com.fasterxml.jackson.core:jackson-annotations")
  api("com.fasterxml.jackson.module:jackson-module-kotlin")
  api("com.google.code.gson:gson:${V.Util.gson}")
  api("ognl:ognl:${V.Util.ognl}")
  api("jakarta.servlet:jakarta.servlet-api")
  api("io.swagger.core.v3:swagger-annotations-jakarta:${V.StandardEdition.swaggerAnnotationJakarta}")
  api("org.slf4j:slf4j-api")
  api("org.springframework:spring-webmvc")
  api("com.google.guava:guava:${V.Util.guava}")
  api("jakarta.validation:jakarta.validation-api")
}

tasks.withType<Test> {
  useTestNG {
    suiteXmlFiles.add(File("src/test/resources/testng.xml"))
  }
}
