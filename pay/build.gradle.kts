project.version = V.Compose.pay

dependencies {
  api("org.springframework.boot:spring-boot-starter-validation")
  api("com.github.wechatpay-apiv3:wechatpay-java:${V.PlatformSdk.wechatpayJava}")
  implementation(project(":core"))
  implementation(project(":depend:depend-web-client"))
  implementation(project(":security:security-oauth2"))
}

tasks.withType<Test> {
  useTestNG {
    suiteXmlFiles.add(File("src/test/resources/testng.xml"))
  }
}
