version = libs.versions.composeDataCrawler.get()

dependencies {
  implementation(project(":core"))

  implementation(libs.bundles.selenium)

  api(libs.com.microsoft.playwright.playwright)

  implementation(libs.com.github.magese.ikAnalyzer)
  implementation(libs.com.github.haifengl.smileMath)
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
