plugins {
  `kotlinspring-convention`
}

project.version = libs.versions.compose.depend.xxl.job.get()

dependencies {
  api(libs.com.xuxueli.xxl.job.core)
  implementation(projects.core)
}
