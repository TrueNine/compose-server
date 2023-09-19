import net.yan100.compose.plugin.V

project.version = libs.versions.compose.secuirty.asProvider().get()


dependencies {
  api("org.springframework.boot:spring-boot-starter-security")
  api("cn.hutool:hutool-captcha:${V.Util.huTool}")
  api("com.auth0:java-jwt:${V.Web.auth0JavaJwt}")
  implementation("org.springframework:spring-webmvc")
  implementation("org.owasp.antisamy:antisamy:${V.Security.antisamy}") {
    exclude(group = "net.sourceforge.nekohtml", module = "nekohtml")
    implementation("net.sourceforge.nekohtml:nekohtml:${V.Security.nekohtml}")
  }
  implementation(project(":core"))
}



tasks.withType<Test> {
  useTestNG {
    suiteXmlFiles.add(File("src/test/resources/testng.xml"))
  }
}

