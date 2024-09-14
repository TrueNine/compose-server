project.version = libs.versions.compose.securityCrypto.get()

dependencies {
  api(libs.org.springframework.boot.springBootStarterSecurity)

  implementation(libs.cn.hutool.hutoolCaptcha)
  implementation(libs.com.auth0.javaJwt)

  implementation(libs.org.springframework.springWebMvc)

  implementation(libs.org.owasp.antisamy.antisamy) {
    exclude(group = "org.htmlunit", module = "neko-htmlunit")
    implementation(libs.org.htmlunit.nekoHtmlunit)
  }

  implementation(project(":depend:depend-http-exchange"))
  implementation(project(":depend:depend-servlet"))
  implementation(project(":security:security-crypto"))
  implementation(project(":core"))

  testImplementation(project(":test-toolkit"))
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
