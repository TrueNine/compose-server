plugins {
  `kotlin-convention`
}

version = libs.versions.compose.data.get()

dependencies {
  api(projects.shared)

  implementation(libs.bundles.selenium)
  api(libs.com.microsoft.playwright.playwright)

  implementation(libs.com.github.magese.ik.analyzer)
  implementation(libs.com.github.haifengl.smile.math)

  testImplementation(projects.testtoolkit)
}
