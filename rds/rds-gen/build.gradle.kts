version = libs.versions.compose.asProvider().get()

dependencies {
  implementation(libs.org.freemarker.freemarker)
  implementation(libs.cn.hutool.hutool.db)
  runtimeOnly(libs.com.mysql.mysql.connector.j)
  implementation(project(":core"))
  implementation(project(":rds"))
  implementation(project(":rds:rds-core"))
}
