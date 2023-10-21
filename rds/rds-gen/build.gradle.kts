version = libs.versions.compose.get()

dependencies {
  implementation(libs.util.freemarker)
  implementation(libs.db.hutool.db)
  runtimeOnly(libs.db.mysql.j)
  implementation(project(":core"))
  implementation(project(":rds"))
}
