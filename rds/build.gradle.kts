project.version = V.Project.rds

dependencies {
  api("org.springframework.boot:spring-boot-starter-validation")
  api("jakarta.validation:jakarta.validation-api:${V.Api.jakartaValidation}")
  api("jakarta.persistence:jakarta.persistence-api")
  api("org.springframework.boot:spring-boot-starter-data-jpa")
  api("org.hibernate:hibernate-spatial:${V.Jpa.hibernate}")
  implementation(project(":core"))
  implementation("org.springframework.boot:spring-boot-starter-web")
  testRuntimeOnly("com.mysql:mysql-connector-j:${V.Driver.mysql}")
}
