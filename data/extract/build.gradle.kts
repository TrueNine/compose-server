version = libs.versions.compose.dataExtract.get()

dependencies {
  api(libs.org.jsoup.jsoup)
  api(libs.net.sf.supercsv.superCsv)

  testImplementation(libs.net.sf.sevenzipjbinding.sevenzipjbinding)
  testImplementation(libs.net.sf.sevenzipjbinding.sevenzipjbindingAllPlatforms)
  api(libs.com.alibaba.easyexcel) {
    exclude("org.apache.commons", "commons-compress")
    implementation(libs.org.apache.commons.commonsCompress)
  }
  implementation(project(":core"))
  implementation(project(":depend:depend-http-exchange"))
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
