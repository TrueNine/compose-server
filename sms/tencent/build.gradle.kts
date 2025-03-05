plugins {
  `kotlinspring-convention`
}

version = libs.versions.compose.sms.tencent.get()

dependencies {
  api(libs.com.tencentcloudapi.tencentcloud.sdk.java.sms) {
    exclude(
      group = libs.com.squareup.okio.okio.get().module.group,
      module = libs.com.squareup.okio.okio.get().module.name,
    )
    exclude(
      group = libs.com.squareup.okhttp3.okhttp.get().module.group,
      module = libs.com.squareup.okhttp3.okhttp.get().module.name,
    )
  }
  implementation(libs.com.squareup.okhttp3.okhttp)
  
  testImplementation(projects.testtoolkit)
}
