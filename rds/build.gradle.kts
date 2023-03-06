project.version = V.Project.rds


dependencies {
  api("org.flywaydb:flyway-core")
  api("org.flywaydb:flyway-mysql")
  api("org.springframework.boot:spring-boot-starter-validation")
  implementation("${group}:core:${V.Project.core}")
  api("jakarta.validation:jakarta.validation-api:${V.Api.jakartaValidation}")
  api("jakarta.persistence:jakarta.persistence-api")
  api("org.springframework.boot:spring-boot-starter-data-jpa")
  api("org.hibernate:hibernate-spatial:${V.Jpa.hibernate}")
  implementation("org.springframework.boot:spring-boot-starter-web")

  runtimeOnly("com.mysql:mysql-connector-j:${V.Driver.mysql}")
}
