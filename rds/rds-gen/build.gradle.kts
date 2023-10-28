version = libs.versions.compose.asProvider().get()

dependencies {
  implementation(libs.util.freemarker)
  implementation(libs.util.hutool.db)
  runtimeOnly(libs.db.mysql.j)
  implementation(project(":core"))
  implementation(project(":rds"))
}
