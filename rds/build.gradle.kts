project.version = V.Compose.rds

dependencies {
  api("org.springframework.boot:spring-boot-starter-data-jpa")
  implementation("jakarta.validation:jakarta.validation-api")
  implementation("org.springframework:spring-web")
  implementation("cn.hutool:hutool-core:${V.Util.huTool}")
  implementation(project(":core"))
  implementation(project(":data-common:data-common-data-extract"))

  testImplementation("org.springframework.boot:spring-boot-starter-validation")
  testImplementation(project(":depend:depend-flyway"))
  testImplementation("com.github.gavlyukovskiy:p6spy-spring-boot-starter:${V.Driver.p6spySpringBootStarter}")
  testImplementation("org.hsqldb:hsqldb:${V.Driver.hsqldb}")
}

tasks.withType<Test> {
  useTestNG {
    suiteXmlFiles.add(File("src/test/resources/testng.xml"))
  }
}
