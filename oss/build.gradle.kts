plugins {
  alias(libs.plugins.org.jetbrains.kotlin.plugin.lombok)
}

version = libs.versions.compose.oss.get()

dependencies {
  // TODO 准备移除
  annotationProcessor(libs.org.projectlombok.lombok)

  implementation(libs.io.minio.minio) {
    exclude(group = "org.apache.logging.log4j", module = "log4j-api")
    exclude(group = "org.apache.logging.log4j", module = "log4j-core")
  }

  implementation(libs.com.aliyun.oss.aliyunSdkOss)
  implementation(libs.com.huaweicloud.esdkObsJava)
  implementation(libs.org.springframework.boot.springBootStarterWeb)

  implementation(project(":depend:depend-http-exchange"))
  implementation(project(":core"))

  compileOnly(libs.org.projectlombok.lombok)
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
