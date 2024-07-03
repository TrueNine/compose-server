project.version = libs.versions.compose.security.get()

dependencies {
  api(libs.org.springframework.boot.springBootStarterSecurity)
  implementation(libs.cn.hutool.hutoolCaptcha)
  implementation(libs.com.auth0.javaJwt)
  implementation(libs.org.springframework.springWebMvc)
  implementation(libs.org.owasp.antisamy.antisamy) {
    // exclude(group = "org.htmlunit:neko-htmlunit", module = "neko-htmlunit")
    // implementation(libs.crawler.nekohtml)
  }
  implementation(project(":depend:depend-web-servlet"))
  implementation(project(":core"))
}

publishing {
  publications {
    create<MavenPublication>("maven") {
      groupId = project.group.toString()
      artifactId = project.name
      version = project.version.toString()
      from(components["java"])
    }
  }
}

signing {
  useGpgCmd()
  sign(publishing.publications["maven"])
}
