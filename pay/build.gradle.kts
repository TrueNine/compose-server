project.version = V.Compose.pay

dependencies {
  api("org.springframework.boot:spring-boot-starter-validation")
  api("com.github.wechatpay-apiv3:wechatpay-java:${V.PlatformSdk.wechatpayJava}")
  implementation(("org.springframework.boot:spring-boot-starter-web"))
  implementation(("org.springframework.boot:spring-boot-starter-webflux"))
  implementation(project(":core"))
}

tasks.withType<Test> {
  useTestNG {
    suiteXmlFiles.add(File("src/test/resources/testng.xml"))
  }
}
