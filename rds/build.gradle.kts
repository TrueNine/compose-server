version = libs.versions.compose.asProvider().get()

dependencies {
  api(libs.bundles.spring.boot.data.jpa)

  testImplementation(libs.bundles.p6spystarter)

  implementation(libs.jakarta.validation.api)
  implementation(libs.spring.webmvc)
  implementation(libs.util.hutool.core)
  testImplementation(libs.spring.boot.validation)
  testImplementation(libs.db.mysql.j)

  implementation(project(":core"))
  testImplementation(project(":depend:depend-flyway"))
}

tasks.withType<Test> {
  useTestNG {
    suiteXmlFiles.add(File("src/test/resources/testng.xml"))
  }
}
//hibernate {
//  enhancement {
//    enableAssociationManagement.set(true)
//  }
//}
