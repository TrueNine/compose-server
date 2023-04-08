project.version = V.Component.rdsGen
dependencies {
  implementation(project(":core"))
  api("org.freemarker:freemarker:${V.Util.freemarker}")
  api("cn.hutool:hutool-db:${V.Util.huTool}")
  runtimeOnly("com.mysql:mysql-connector-j:${V.Driver.mysqlConnectorJ}")
}
