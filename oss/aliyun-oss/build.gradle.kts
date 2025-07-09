plugins {
  id("buildlogic.kotlinspring-conventions")
}

dependencies {
  api(projects.oss.ossShared)
  api(projects.shared)
  implementation(libs.com.aliyun.oss.aliyun.sdk.oss)
  testImplementation(projects.testtoolkit)
}
