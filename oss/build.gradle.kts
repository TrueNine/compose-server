version = libs.versions.compose.oss.get()

dependencies {
  implementation(libs.sdk.oss.minio) {
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

tasks {
  test {
    useTestNG {
      suiteXmlFiles.add(File("src/test/resources/testng.xml"))
    }
  }
}
