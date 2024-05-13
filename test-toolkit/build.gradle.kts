version = libs.versions.compose.asProvider().get()

dependencies {
  api(libs.bundles.knife4j)
  implementation(project(":core"))

  api(libs.org.springdoc.springdoc.openapi.starter.webmvc.ui)
  api(libs.io.swagger.core.v3.swagger.annotations.jakarta)
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
