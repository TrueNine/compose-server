plugins { `kotlinspring-convention` }

version = libs.versions.compose.oss.get()

dependencies {
  implementation(libs.io.minio.minio) {
    exclude(group = "org.apache.logging.log4j", module = "log4j-api")
    exclude(group = "org.apache.logging.log4j", module = "log4j-core")
  }

  implementation(libs.com.aliyun.oss.aliyun.sdk.oss)
  implementation(libs.com.huaweicloud.esdk.obs.java)

  implementation(projects.core)
  implementation(projects.depend.dependHttpExchange)

  testImplementation(projects.testtoolkit)
  testImplementation(libs.org.springframework.boot.spring.boot.starter.web)
}
