project.version = V.Component.pay

dependencies {
  implementation(("cn.hutool:hutool-core:${V.Util.huTool}"))
  implementation(("org.springframework.boot:spring-boot-starter-webflux"))
  implementation("com.github.wechatpay-apiv3:wechatpay-java:${V.PlatformSdk.wechatpayJava}")
  implementation(project(":core"))
}

tasks.withType<Test> {
  useTestNG {
    suiteXmlFiles.add(File("src/test/resources/testng.xml"))
  }
}
