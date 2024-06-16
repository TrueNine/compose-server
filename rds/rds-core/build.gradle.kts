version = libs.versions.compose.rds.core.get()

dependencies {
  implementation(project(":core"))
  implementation(project(":depend:depend-jvalid"))
  implementation(libs.spring.data.springDataCommons)

  testImplementation(project(":depend:depend-web-servlet"))
  testImplementation(libs.org.springframework.boot.spring.boot.starter.json)

  kapt(variantOf(libs.com.querydsl.querydsl.apt) { classifier("jakarta") })
  implementation(variantOf(libs.com.querydsl.querydsl.jpa) { classifier("jakarta") })
}

hibernate {
  enhancement {
    enableAssociationManagement.set(true)
    enableDirtyTracking.set(true)
    enableLazyInitialization.set(true)
  }
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
