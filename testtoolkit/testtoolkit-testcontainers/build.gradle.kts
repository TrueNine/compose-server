plugins {
  id("buildlogic.kotlinspring-conventions")
  id("buildlogic.spotless-conventions")
}

description =
  """
  TestContainers specialized testing toolkit providing containerized integration test support.
  Includes advanced container orchestration, specialized database/cache/storage test utilities, and container lifecycle management.
  """
    .trimIndent()

dependencies {
  api(projects.testtoolkit.testtoolkitShared)

  api(libs.org.testcontainers.testcontainers)
  api(libs.org.testcontainers.postgresql)
  api(libs.org.testcontainers.mysql)
  api(libs.org.testcontainers.junit.jupiter)
  api(libs.org.testcontainers.minio)
  api(libs.io.minio.minio)

  // 测试依赖
  testImplementation(libs.org.springframework.boot.spring.boot.starter.jdbc)
  testImplementation(libs.org.springframework.boot.spring.boot.starter.data.redis)
  testRuntimeOnly(libs.org.postgresql.postgresql)
  testRuntimeOnly(libs.com.mysql.mysql.connector.j)
}
