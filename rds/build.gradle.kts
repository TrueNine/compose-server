project.version = V.Component.rds

dependencies {
  api("jakarta.validation:jakarta.validation-api:${V.StandardEdition.jakartaValidationApi}")
  api("jakarta.persistence:jakarta.persistence-api:${V.StandardEdition.jakartPersistenceApi}")
  api("org.springframework.boot:spring-boot-starter-validation")

  api("org.springframework.boot:spring-boot-starter-data-jpa")
  implementation(project(":core"))
  testImplementation(project(":depend:depend-flyway"))

  implementation("cn.hutool:hutool-core:${V.Util.huTool}")
  testRuntimeOnly("com.mysql:mysql-connector-j:${V.Driver.mysqlConnectorJ}")
  testImplementation("com.github.gavlyukovskiy:p6spy-spring-boot-starter:1.9.0")
}
