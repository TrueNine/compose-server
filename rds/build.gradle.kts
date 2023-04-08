project.version = V.Component.rds

dependencies {
  api("jakarta.validation:jakarta.validation-api")
  api("org.springframework.boot:spring-boot-starter-validation")

  api("org.springframework.boot:spring-boot-starter-data-jpa")
  implementation(project(":core"))
  testImplementation(project(":depend:depend-flyway"))

  implementation("cn.hutool:hutool-core")
  testRuntimeOnly("com.mysql:mysql-connector-j")
  testImplementation("com.github.gavlyukovskiy:p6spy-spring-boot-starter")
}
