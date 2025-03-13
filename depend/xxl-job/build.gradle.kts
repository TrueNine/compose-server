plugins { `kotlinspring-convention` }

project.version = libs.versions.compose.depend.get()

dependencies {
  api(libs.com.xuxueli.xxl.job.core)
  implementation(projects.core)
}
