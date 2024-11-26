version = libs.versions.composeCacheable.get()

dependencies {

  implementation(libs.bundles.redis)
  implementation(libs.com.github.benManes.caffeine.caffeine)

  implementation(project(":depend:depend-jackson"))
  implementation(project(":core"))

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
