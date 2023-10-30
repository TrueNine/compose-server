version = libs.versions.compose.asProvider().get()

dependencies {
  implementation(libs.util.freemarker)
  implementation(libs.util.hutool.db)
  runtimeOnly(libs.db.mysqlJ)
  implementation(project(":core"))
  implementation(project(":rds"))
}
