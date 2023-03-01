dependencies {
  api("org.springframework.boot:spring-boot-starter-validation")
  api(V.Component.pkgV("core"))
  api("jakarta.validation:jakarta.validation-api:${V.Api.jakartaValidation}")
  api("jakarta.persistence:jakarta.persistence-api")
  api("org.springframework.boot:spring-boot-starter-data-jpa")
  api("org.hibernate:hibernate-spatial:${V.Jpa.hibernate}")
  implementation("org.springframework.boot:spring-boot-starter-web")

  runtimeOnly("com.mysql:mysql-connector-j:${V.Driver.mysql}")
}
