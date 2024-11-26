plugins {
  alias(libs.plugins.org.jetbrains.kotlin.jvm)
}

version = libs.versions.composeSecuritySpring.get()

dependencies {
  implementation(libs.org.springframework.boot.springBootAutoconfigure)
  kapt(libs.org.springframework.springBootConfigurationProcessor)


  api(libs.org.springframework.boot.springBootStarterSecurity)

  implementation(libs.cn.hutool.hutoolCaptcha)
  implementation(libs.com.auth0.javaJwt)

  implementation(libs.com.fasterxml.jackson.core.jacksonDatabind)

  implementation(libs.org.springframework.springWebMvc)

  implementation(libs.org.owasp.antisamy.antisamy) {
    exclude(group = "org.htmlunit", module = "neko-htmlunit")
    implementation(libs.org.htmlunit.nekoHtmlunit)
  }

  //implementation(project(":depend:depend-http-exchange"))
  // TODO 剥离 web模块
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
