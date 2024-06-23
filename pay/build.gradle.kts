version = libs.versions.compose.asProvider().get()

dependencies {
  api(libs.sdk.pay.wechatv3)
  implementation(libs.jakarta.servlet.jakarta.servlet.api)
  implementation(libs.spring.boot.validation)
  implementation(project(":core"))
  implementation(project(":depend:depend-web-client"))
  implementation(project(":security:security-oauth2"))
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
