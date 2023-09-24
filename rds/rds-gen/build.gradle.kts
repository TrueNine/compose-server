import net.yan100.compose.plugin.V

version = libs.versions.compose.rds.gen.get()

dependencies {
  implementation(libs.util.freemarker)
  implementation(libs.db.hutool.db)
  runtimeOnly(libs.db.mysql.j)
  implementation(project(":core"))
  implementation(project(":rds"))
}
