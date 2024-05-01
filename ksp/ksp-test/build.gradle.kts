plugins {
  alias(libs.plugins.ktJvm)
  alias(libs.plugins.ktKsp)
}

dependencies {
  ksp(project(":ksp"))
  implementation(project(":ksp"))
  implementation(project(":rds"))
  implementation(project(":core"))
  implementation(project(":rds:rds-core"))
}
