dependencies {
  implementation(libs.db.mysqlJ)
  implementation(project(":core"))
  implementation(project(":rds"))
  implementation(project(":security"))
  implementation(project(":depend:depend-web-servlet"))

}
