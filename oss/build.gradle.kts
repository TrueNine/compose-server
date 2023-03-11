project.version = V.Project.oss

dependencies {
  api("io.minio:minio:${V.Driver.minio}")
  implementation(project(":core"))
  implementation("org.springframework.boot:spring-boot-starter-web")
  api("com.aliyun.oss:aliyun-sdk-oss:${V.Sdk.aliYunOss}")
}
