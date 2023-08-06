import net.yan100.compose.plugin.V

project.version = V.Compose.rdsGen
dependencies {
  implementation(project(":core"))
  implementation(project(":rds"))
  implementation("org.freemarker:freemarker:${V.Util.freemarker}")
  implementation("cn.hutool:hutool-db:${V.Util.huTool}")
  runtimeOnly("com.mysql:mysql-connector-j:${V.Driver.mysqlConnectorJ}")
}
