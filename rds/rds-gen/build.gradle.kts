version = libs.versions.compose.get()

dependencies {
  implementation(libs.util.freemarker)
  implementation(libs.util.hutoolDb)
  runtimeOnly(libs.db.mysqlJ)
  implementation(project(":core"))
  implementation(project(":rds"))
  implementation(project(":rds:rds-core"))
}
