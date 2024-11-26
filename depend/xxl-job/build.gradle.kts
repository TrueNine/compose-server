project.version = libs.versions.composeDependXxlJob.get()

dependencies {
  implementation(libs.org.springframework.boot.springBootAutoconfigure)
  kapt(libs.org.springframework.springBootConfigurationProcessor)

  api(libs.com.xuxueli.xxlJobCore)
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
