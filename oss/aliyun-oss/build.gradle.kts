plugins { `kotlinspring-convention` }

version = libs.versions.compose.oss.get()

dependencies {
  implementation(projects.oss.ossShared)
  implementation(libs.com.aliyun.oss.aliyun.sdk.oss)
  testImplementation(projects.testtoolkit)
}
