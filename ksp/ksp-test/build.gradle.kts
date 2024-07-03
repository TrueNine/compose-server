plugins {
  alias(libs.plugins.org.jetbrains.kotlin.jvm)
  alias(libs.plugins.com.google.devtools.ksp)
}

dependencies {
  ksp(project(":ksp"))
  implementation(project(":ksp:ksp-core"))
  implementation(project(":rds"))
  implementation(project(":core"))
  implementation(project(":rds:rds-core"))
}
