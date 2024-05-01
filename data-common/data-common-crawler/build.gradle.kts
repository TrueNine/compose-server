version = libs.versions.compose.get()

dependencies {
  api(project(":data-common:data-common-data-extract"))
  implementation(project(":core"))
  api(libs.bundles.selenium)
  api(libs.com.microsoft.playwright.playwright)
  implementation(libs.util.ikanalyzer)
  implementation(libs.util.smilemath)
}
