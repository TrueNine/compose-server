package io.github.truenine.composeserver.testtoolkit

object SpringBootConfigurationPropertiesPrefixes {
  const val ROOT = "compose"
  const val TESTTOOLKIT = "$ROOT.testtoolkit"
  const val TESTTOOLKIT_TESTCONTAINERS = "$TESTTOOLKIT.testcontainers"
  const val OSS = "$ROOT.oss"
  const val OSS_MINIO = "$OSS.minio"
  const val OSS_MINIO_ENDPOINT = "$OSS_MINIO.endpoint"
  const val OSS_MINIO_PORT = "$OSS_MINIO.port"
  const val OSS_MINIO_ACCESS_KEY = "$OSS_MINIO.access-key"
  const val OSS_MINIO_SECRET_KEY = "$OSS_MINIO.secret-key"
  const val OSS_MINIO_EXPOSED_BASE_URL = "$OSS_MINIO.exposed-base-url"
}
