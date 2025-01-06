plugins {
  `kotlinspring-convention`
}

project.version = libs.versions.composeDependXxlJob.get()

dependencies {
  api(libs.com.xuxueli.xxlJobCore)
  implementation(projects.core)
}
