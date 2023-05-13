project.version = V.Compose.core

dependencies {
  api("com.fasterxml.jackson.core:jackson-annotations")
  api("com.fasterxml.jackson.module:jackson-module-kotlin")
  api("ognl:ognl:${V.Util.ognl}")
  api("com.google.guava:guava:${V.Util.guava}")
  api("jakarta.servlet:jakarta.servlet-api")
  api("io.swagger.core.v3:swagger-annotations-jakarta:${V.StandardEdition.swaggerAnnotationJakarta}")
  api("org.slf4j:slf4j-api")
  api("jakarta.validation:jakarta.validation-api")
  implementation("org.springframework.boot:spring-boot-starter-json")
  implementation("org.springframework.security:spring-security-crypto")
  implementation("org.bouncycastle:bcprov-jdk15to18:${V.Security.bcprovJdk15to18}")
  implementation("org.springframework:spring-webmvc")
  // kotlin
  api("org.jetbrains.kotlin:kotlin-stdlib-jdk8:${V.Lang.kotlin}")
  api("org.jetbrains.kotlin:kotlin-reflect:${V.Lang.kotlin}")
  api("org.jetbrains.kotlinx:kotlinx-coroutines-reactor:${V.Lang.kotlinxCoroutine}")
  api("io.projectreactor.kotlin:reactor-kotlin-extensions:${V.Lang.reactorKotlinExtension}")
  api("org.jetbrains:annotations:${V.Lang.jetbrainsAnnotations}")

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
