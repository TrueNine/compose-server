version = libs.versions.compose.asProvider().get()

dependencies {
  api(libs.bundles.spring.redis)
  api(libs.com.github.ben.manes.caffeine.caffeine)
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
