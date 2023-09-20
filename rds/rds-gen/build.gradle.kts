import net.yan100.compose.plugin.V

version = libs.versions.compose.rds.gen.get()

dependencies {
  implementation(project(":core"))
  implementation(project(":rds"))
  implementation("org.freemarker:freemarker:${V.Util.freemarker}")
  implementation("cn.hutool:hutool-db:${V.Util.huTool}")
  runtimeOnly(libs.db.mysql.j)
}
