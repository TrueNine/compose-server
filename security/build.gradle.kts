project.version = libs.versions.compose.asProvider().get()

dependencies {
  api(libs.spring.boot.security)
  implementation(libs.cn.hutool.hutool.captcha)
  implementation(libs.security.auth0Jwt)
  implementation(libs.jakarta.servletApi)
  implementation(libs.spring.webmvc)
  implementation(libs.security.antisamy) {
    // exclude(group = "org.htmlunit:neko-htmlunit", module = "neko-htmlunit")
    // implementation(libs.crawler.nekohtml)
  }
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
