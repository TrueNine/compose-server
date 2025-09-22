package itest.integrate.oss.volcenginetos

import io.github.truenine.composeserver.logger
import io.github.truenine.composeserver.oss.volcengine.properties.VolcengineTosProperties

val log = logger<Any>()

private val accessKey = System.getenv("VOLCENGINE_TOS_ACCESS_KEY")?.takeIf { it.isNotBlank() }
private val secretKey = System.getenv("VOLCENGINE_TOS_SECRET_KEY")?.takeIf { it.isNotBlank() }
private val hasCredentials = !accessKey.isNullOrBlank() && !secretKey.isNullOrBlank()

data class OssAkSk(val ak: String, val sk: String, val endpoint: String = VolcengineTosProperties.DEFAULT_ENDPOINT)

fun hasTosRequiredEnvironmentVariables(): Boolean {
  if (!hasCredentials) {
    log.warn("Skipping Volcengine TOS integration tests: missing required environment variables VOLCENGINE_TOS_ACCESS_KEY or VOLCENGINE_TOS_SECRET_KEY")
  } else {
    log.info("Detected Volcengine TOS credentials, will execute integration tests")
  }
  return hasCredentials
}

fun getTosAkSk(): OssAkSk? {
  return if (hasTosRequiredEnvironmentVariables()) {
    OssAkSk(accessKey!!, secretKey!!)
  } else {
    null
  }
}
