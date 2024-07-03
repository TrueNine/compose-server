version = libs.versions.compose.asProvider().get()

dependencies {
  implementation(libs.io.minio.minio) {
    exclude(group = "org.apache.logging.log4j", module = "log4j-api")
    exclude(group = "org.apache.logging.log4j", module = "log4j-core")
  }

  implementation(libs.com.aliyun.oss.aliyunSdkOss)
  implementation(libs.com.huaweicloud.esdkObsJava)
  implementation(libs.org.springframework.boot.springBootStarterWeb)

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
