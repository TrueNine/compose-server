dependencies {
  api("io.minio:minio:${V.Driver.minio}")
  api(V.Component.pkgV("core"))
  implementation("org.springframework.boot:spring-boot-starter-web")
  api("com.aliyun.oss:aliyun-sdk-oss:${V.Sdk.aliYunOss}")
}
