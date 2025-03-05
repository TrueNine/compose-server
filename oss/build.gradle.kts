plugins { `kotlinspring-convention` }

version = libs.versions.compose.oss.get()

dependencies {
  implementation(libs.io.minio.minio) {
    exclude(
      group = libs.com.squareup.okhttp3.okhttp.get().group,
      module = libs.com.squareup.okhttp3.okhttp.get().group,
    )
    exclude(
      group = libs.org.apache.logging.log4j.log4j.api.get().group,
      module = libs.org.apache.logging.log4j.log4j.api.get().name
    )
    exclude(
      group = libs.org.apache.logging.log4j.log4j.core.get().group,
      module = libs.org.apache.logging.log4j.log4j.core.get().name
    )
  }

  runtimeOnly(libs.org.apache.logging.log4j.log4j.core)
  runtimeOnly(libs.org.apache.logging.log4j.log4j.api)
  runtimeOnly(libs.com.squareup.okhttp3.okhttp)

  implementation(libs.com.aliyun.oss.aliyun.sdk.oss)
  implementation(libs.com.huaweicloud.esdk.obs.java) {
    exclude(
      group = libs.com.squareup.okhttp3.okhttp.get().group,
      module = libs.com.squareup.okhttp3.okhttp.get().group,
    )
  }

  implementation(projects.core)
  implementation(projects.depend.dependHttpExchange)

  testImplementation(projects.testtoolkit)
  testImplementation(libs.org.springframework.boot.spring.boot.starter.web)
}
