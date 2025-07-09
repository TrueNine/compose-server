plugins {
  id("buildlogic.kotlinspring-conventions")
}

dependencies {
  api(projects.oss.ossShared)
  api(projects.shared)
  implementation(libs.com.huaweicloud.esdk.obs.java)
  testImplementation(projects.testtoolkit)
}
