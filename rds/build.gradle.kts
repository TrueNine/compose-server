version = libs.versions.compose.asProvider().get()

dependencies {
  api(libs.bundles.spring.boot.data.jpa)

  testImplementation(libs.bundles.p6spystarter)

  implementation(libs.spring.security.crypto)
  implementation(libs.jakarta.validation.api)
  implementation(libs.spring.webmvc)
  implementation(libs.util.hutool.core)
  testImplementation(libs.spring.boot.validation)
  testImplementation(libs.db.mysql.j)

  implementation(project(":core"))
  testImplementation(project(":depend:depend-flyway"))
}

//hibernate {
//  enhancement {
//    enableAssociationManagement.set(true)
//  }
//}
