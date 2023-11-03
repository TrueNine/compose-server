version = libs.versions.compose.asProvider().get()

dependencies {
  api(libs.bundles.spring.jpa)
  api(project(":rds:rds-core"))

  testImplementation(libs.bundles.p6spySpring)

  implementation(libs.spring.security.crypto)
  implementation(libs.jakarta.validationApi)
  implementation(libs.spring.webmvc)
  implementation(libs.util.hutool.core)
  testImplementation(libs.spring.boot.validation)
  testImplementation(libs.db.mysqlJ)

  implementation(project(":core"))
  testImplementation(project(":depend:depend-flyway"))
}

//hibernate {
//  enhancement {
//    enableAssociationManagement.set(true)
//  }
//}
