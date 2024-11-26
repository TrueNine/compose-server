version = libs.versions.composePay.get()

dependencies {
  api(libs.com.github.wechatpayApiv3.wechatpayJava)

  implementation(project(":core"))
  implementation(project(":depend:depend-http-exchange"))
  implementation(project(":security:security-oauth2"))
  implementation(project(":security:security-crypto"))

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
