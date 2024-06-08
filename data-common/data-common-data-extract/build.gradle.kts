version = libs.versions.compose.asProvider().get()

dependencies {
  api(libs.crawler.jsoup)
  api(libs.crawler.supercsv)
  api(libs.util.easyexcel) {
    exclude("org.apache.commons", "commons-compress")
    implementation(libs.org.apache.commons.commons.compress)
  }
  implementation(project(":core"))
  implementation(project(":depend:depend-web-client"))
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
