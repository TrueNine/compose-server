version = libs.versions.compose.asProvider().get()

dependencies {
  api(libs.org.jsoup.jsoup)
  api(libs.net.sf.supercsv.superCsv)
  api(libs.com.alibaba.easyexcel) {
    exclude("org.apache.commons", "commons-compress")
    implementation(libs.org.apache.commons.commonsCompress)
  }
  implementation(project(":core"))
  implementation(project(":depend:depend-http-exchange"))
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
