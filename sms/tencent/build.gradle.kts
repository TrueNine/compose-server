plugins { id("buildlogic.kotlinspring-conventions") }

version = libs.versions.compose.sms.get()

dependencies {
  api(libs.com.tencentcloudapi.tencentcloud.sdk.java.sms) {
    exclude(
      group = libs.com.squareup.okio.okio.asProvider().get().module.group,
      module = libs.com.squareup.okio.okio.asProvider().get().module.name,
    )
    exclude(
      group = libs.com.squareup.okhttp3.okhttp.get().module.group,
      module = libs.com.squareup.okhttp3.okhttp.get().module.name,
    )
  }
  implementation(libs.com.squareup.okhttp3.okhttp)

  testImplementation(projects.testtoolkit)
}
