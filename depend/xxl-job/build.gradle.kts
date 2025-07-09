plugins {
  id("buildlogic.kotlinspring-conventions")
}

project.version = libs.versions.compose.depend.get()

dependencies {
  api(libs.com.xuxueli.xxl.job.core)
  api(projects.shared)
}
