project.version = V.Component.pay

dependencies {
  implementation(project(":core"))
  implementation(("cn.hutool:hutool-core:${V.Util.huTool}"))
  implementation("com.github.wechatpay-apiv3:wechatpay-java:0.2.7")
}

tasks.withType<Test> {
  useTestNG {
    suiteXmlFiles.add(File("src/test/resources/testng.xml"))
  }
}
