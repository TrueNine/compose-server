version = libs.versions.compose.oss.get()

dependencies {
  implementation(libs.io.minio.minio) {
    exclude(group = "org.apache.logging.log4j", module = "log4j-api")
    exclude(group = "org.apache.logging.log4j", module = "log4j-core")
  }

  implementation(libs.com.aliyun.oss.aliyunSdkOss)
  implementation(libs.com.huaweicloud.esdkObsJava)
  implementation(libs.org.springframework.boot.springBootStarterWeb)

  implementation(project(":depend:depend-http-exchange"))
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
