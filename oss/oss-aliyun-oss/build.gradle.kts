plugins {
  id("buildlogic.kotlinspring-conventions")
  id("buildlogic.spotless-conventions")
}

description =
  """
  Alibaba Cloud OSS (Object Storage Service) integration for scalable cloud storage operations.
  Provides seamless integration with Aliyun OSS including authentication, bucket operations, and file management.
  """
    .trimIndent()

dependencies {
  api(projects.oss.ossShared)
  api(projects.shared)
  implementation(libs.com.aliyun.oss.aliyun.sdk.oss)

  testImplementation(projects.testtoolkit.testtoolkitShared)
}
