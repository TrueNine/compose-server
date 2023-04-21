project.version = V.Component.oss

dependencies {
  implementation("io.minio:minio:${V.PlatformSdk.minio}") {
    exclude(group = "org.apache.logging.log4j", module = "log4j-api")
    exclude(group = "org.apache.logging.log4j", module = "log4j-core")
  }
  implementation(project(":depend:depend-web-client"))

  implementation("com.aliyun.oss:aliyun-sdk-oss:${V.PlatformSdk.aliYunOss}")
  implementation("com.huaweicloud:esdk-obs-java:${V.PlatformSdk.huaweiObsJava}")
  implementation(project(":core"))
  implementation("org.springframework.boot:spring-boot-starter-web")
}
configurations.all {
  exclude("org.apache.logging.log4j","log4j-core")
  exclude("org.apache.logging.log4j","log4j-api")
}

tasks.withType<Test> {
  useTestNG {
    suiteXmlFiles.add(File("src/test/resources/testng.xml"))
  }
}
