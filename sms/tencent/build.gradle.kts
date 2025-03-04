plugins { `kotlinspring-convention` }

version = libs.versions.compose.sms.tencent.get()

dependencies {
  api(libs.com.tencentcloudapi.tencentcloud.sdk)
}
