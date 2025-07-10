plugins { 
  id("buildlogic.kotlinspring-conventions")
  id("buildlogic.spotless-conventions")
}

description = """
Tencent Cloud SMS service integration for reliable message delivery.
Provides SMS sending capabilities through Tencent Cloud SMS API with template support and delivery tracking.
""".trimIndent()


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
