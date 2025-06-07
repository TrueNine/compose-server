plugins {
  `kotlinspring-convention`
}

version = libs.versions.compose.oss.get()

dependencies {
  api(projects.oss.ossShared)
  api(projects.shared)
  implementation(libs.com.huaweicloud.esdk.obs.java)
  testImplementation(projects.testtoolkit)
}
