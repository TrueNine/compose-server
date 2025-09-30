plugins {
  id("buildlogic.kotlinspring-conventions")
  id("buildlogic.spotless-conventions")
}

description =
  """
  Huawei Cloud OBS (Object Storage Service) integration for enterprise-grade cloud storage.
  Provides comprehensive OBS operations including object management, lifecycle policies, and security configurations.
  """
    .trimIndent()

dependencies {
  api(projects.oss.ossShared)
  api(projects.shared)
  implementation(libs.com.huaweicloud.esdk.obs.java)

  testImplementation(projects.testtoolkit.testtoolkitShared)
}
