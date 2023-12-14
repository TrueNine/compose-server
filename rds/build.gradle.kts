version = libs.versions.compose.asProvider().get()

dependencies {
  api(libs.bundles.spring.jpa)
  api(libs.jakarta.annotationApi)


  kapt(variantOf(libs.querydsl.apt) { classifier("jakarta") })
  api(variantOf(libs.querydsl.jpa) { classifier("jakarta") })
  api(project(":rds:rds-core"))
  api(libs.jakarta.annotationApi)

  implementation(project(":core"))


  testImplementation(libs.bundles.p6spySpring)

  implementation(libs.spring.security.crypto)
  implementation(libs.jakarta.validationApi)
  implementation(libs.spring.webmvc)
  implementation(libs.util.hutool.core)
  testImplementation(libs.spring.boot.validation)
  testImplementation(libs.db.mysqlJ)

  testImplementation(project(":depend:depend-flyway"))
}

//hibernate {
//  enhancement {
//    enableAssociationManagement.set(true)
//  }
//}
