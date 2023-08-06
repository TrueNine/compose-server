import net.yan100.compose.plugin.V

dependencies {
  implementation(project(":core"))
  implementation(project(":rds"))
  implementation(project(":security"))
  implementation(project(":depend:depend-web-servlet"))
  implementation("com.mysql:mysql-connector-j:${V.Driver.mysqlConnectorJ}")

}
