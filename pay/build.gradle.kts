version = libs.versions.compose.get()

dependencies {
  api(libs.sdk.pay.wechatv3)
  implementation(libs.jakarta.servletApi)
  implementation(libs.spring.boot.validation)
  implementation(project(":core"))
  implementation(project(":depend:depend-web-client"))
  implementation(project(":security:security-oauth2"))
}
