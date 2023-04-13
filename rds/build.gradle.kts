project.version = V.Component.rds

dependencies {
  api("jakarta.validation:jakarta.validation-api")
  api("org.springframework.boot:spring-boot-starter-validation")
  api("org.springframework.boot:spring-boot-starter-data-jpa")
  implementation("cn.hutool:hutool-core:${V.Util.huTool}")
  implementation(project(":core"))
  testImplementation(project(":depend:depend-flyway"))
  testApi("com.github.gavlyukovskiy:p6spy-spring-boot-starter:${V.Driver.p6spySpringBootStarter}")
  testRuntimeOnly("com.mysql:mysql-connector-j")
}

tasks.withType<Test> {
  useTestNG {
    suiteXmlFiles.add(File("src/test/resources/testng.xml"))
  }
}
