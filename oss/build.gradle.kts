version = libs.versions.compose.asProvider().get()

dependencies {
  implementation(libs.io.minio.minio) {
    exclude(group = "org.apache.logging.log4j", module = "log4j-api")
    exclude(group = "org.apache.logging.log4j", module = "log4j-core")
  }

  implementation(libs.sdk.oss.aliyun)
  implementation(libs.sdk.oss.huaweicloud)
  implementation(libs.spring.boot.web)

  implementation(project(":depend:depend-web-client"))
  implementation(project(":core"))
}

configurations {
  all {
    exclude("org.apache.logging.log4j", "log4j-core")
    exclude("org.apache.logging.log4j", "log4j-api")
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
