version = libs.versions.compose.asProvider().get()

dependencies {
  api(project(":data-common:data-common-data-extract"))
  implementation(project(":core"))
  api(libs.bundles.selenium)
  api(libs.com.microsoft.playwright.playwright)
  implementation(libs.com.github.magese.ikAnalyzer)
  implementation(libs.com.github.haifengl.smileMath)
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
