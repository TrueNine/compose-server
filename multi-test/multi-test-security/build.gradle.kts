dependencies {
  implementation(libs.db.mysql.j)
  implementation(project(":core"))
  implementation(project(":rds"))
  implementation(project(":security"))
  implementation(project(":depend:depend-web-servlet"))

}
