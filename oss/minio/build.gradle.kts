plugins {
  id("buildlogic.kotlinspring-conventions")
}

description = """
MinIO object storage integration providing S3-compatible storage operations.
Includes bucket management, file operations, and comprehensive testing with TestContainers support.
""".trimIndent()

dependencies {
  api(projects.oss.ossShared)
  api(projects.shared)
  implementation(libs.io.minio.minio) {
    exclude(
      group = libs.com.squareup.okhttp3.okhttp.get().group,
      module = libs.com.squareup.okhttp3.okhttp.get().group,
    )
    exclude(
      group = libs.org.apache.logging.log4j.log4j.api.get().group,
      module = libs.org.apache.logging.log4j.log4j.api.get().name,
    )
    exclude(
      group = libs.org.apache.logging.log4j.log4j.core.get().group,
      module = libs.org.apache.logging.log4j.log4j.core.get().name,
    )
  }
  runtimeOnly(libs.org.apache.logging.log4j.log4j.core)
  runtimeOnly(libs.org.apache.logging.log4j.log4j.api)
  runtimeOnly(libs.com.squareup.okhttp3.okhttp)

  testImplementation(projects.testtoolkit)

  // TestContainers
  testImplementation(libs.org.testcontainers.testcontainers)
  testImplementation(libs.org.testcontainers.minio)
}
