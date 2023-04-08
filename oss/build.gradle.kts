project.version = V.Component.oss

dependencies {
  api("io.minio:minio:${V.PlatformSdk.minio}")
  api("com.aliyun.oss:aliyun-sdk-oss:${V.PlatformSdk.aliYunOss}")
  implementation(project(":core"))
  implementation("org.springframework.boot:spring-boot-starter-web")
}
