project.version = V.Component.rds

dependencies {
  api("jakarta.validation:jakarta.validation-api")
  api("org.springframework.boot:spring-boot-starter-validation")
  api("org.springframework.boot:spring-boot-starter-data-jpa")
  testApi("com.github.gavlyukovskiy:p6spy-spring-boot-starter:${V.Driver.p6spySpringBootStarter}")
  implementation("cn.hutool:hutool-core:${V.Util.huTool}")
  implementation(project(":core"))
  testImplementation(project(":depend:depend-flyway"))

  testImplementation("org.hsqldb:hsqldb:${V.Driver.hsqldb}")
  testImplementation("com.h2database:h2:${V.Driver.h2}")
}

tasks.withType<Test> {
  useTestNG {
    suiteXmlFiles.add(File("src/test/resources/testng.xml"))
  }
}
