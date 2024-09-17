version = libs.versions.compose.cacheable.get()

dependencies {
  api(libs.bundles.redis)
  api(libs.com.github.benManes.caffeine.caffeine)
  implementation(project(":depend:depend-jackson"))
  implementation(project(":core"))
  implementation(project(":test-toolkit"))
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
