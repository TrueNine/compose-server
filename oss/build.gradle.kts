plugins {
  `kotlinspring-convention`
}

version = libs.versions.composeOss.get()

dependencies {
  implementation(libs.io.minio.minio) {
    exclude(group = "org.apache.logging.log4j", module = "log4j-api")
    exclude(group = "org.apache.logging.log4j", module = "log4j-core")
  }

  implementation(libs.com.aliyun.oss.aliyunSdkOss)
  implementation(libs.com.huaweicloud.esdkObsJava)

  implementation(projects.core)
  implementation(projects.depend.dependHttpExchange)

  testImplementation(projects.testToolkit)
  testImplementation(libs.org.springframework.boot.springBootStarterWeb)
}
