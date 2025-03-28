plugins { `kotlinspring-convention` }

version = libs.versions.compose.oss.get()

dependencies {
  implementation(projects.oss.ossCommon)
  implementation(libs.com.huaweicloud.esdk.obs.java)
  testImplementation(projects.testtoolkit)
}
