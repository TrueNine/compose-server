plugins { `kotlinspring-convention` }

version = libs.versions.compose.oss.get()

dependencies {
  implementation(projects.oss.ossCommon)
  implementation(libs.com.aliyun.oss.aliyun.sdk.oss)
  testImplementation(projects.testtoolkit)
}
