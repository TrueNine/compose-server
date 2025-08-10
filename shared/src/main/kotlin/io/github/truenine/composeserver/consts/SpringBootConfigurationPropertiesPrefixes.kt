package io.github.truenine.composeserver.consts

/**
 * Centralized Spring Boot Configuration Properties prefixes for the entire project.
 *
 * This object contains all the configuration property prefixes used across different modules to provide a single source of truth and easier management of
 * configuration namespaces.
 *
 * ## Module Organization
 * - **Shared**: Core shared functionality prefixes
 * - **Security**: Security-related configuration prefixes
 * - **OSS**: Object Storage Service configuration prefixes
 * - **Pay**: Payment service configuration prefixes
 * - **PSDK**: Platform SDK configuration prefixes
 * - **Depend**: Dependency module configuration prefixes
 * - **TestToolkit**: Testing framework configuration prefixes
 *
 * @author TrueNine
 * @since 2025-01-09
 */
object SpringBootConfigurationPropertiesPrefixes {

  // ========== Base Prefixes ==========

  /** Root prefix for all compose server configurations */
  const val ROOT = "compose"

  // ========== Shared Module Prefixes ==========

  /** Base prefix for shared module configurations */
  const val SHARED = "$ROOT.shared"

  /** Snowflake ID generator configuration prefix */
  const val SHARED_SNOWFLAKE = "$SHARED.snowflake"

  /** Data loading configuration prefix */
  const val SHARED_DATA_LOAD = "$SHARED.data-load"

  /** Resource holder configuration prefix */
  const val SHARED_RESOURCE_HOLDER = "$SHARED.resource-holder"

  // ========== Security Module Prefixes ==========

  /** Base prefix for security module configurations */
  const val SECURITY = "$ROOT.security"

  /** JWT configuration prefix */
  const val SECURITY_JWT = "$SECURITY.jwt"

  /** Keys configuration prefix */
  const val SECURITY_KEYS = "$SECURITY.keys"

  // ========== OSS Module Prefixes ==========

  /** Base prefix for OSS module configurations */
  const val OSS = "$ROOT.oss"

  /** MinIO OSS configuration prefix */
  const val OSS_MINIO = "$OSS.minio"

  /** Volcengine TOS configuration prefix */
  const val OSS_VOLCENGINE_TOS = "$OSS.volcengine-tos"

  // ========== Pay Module Prefixes ==========

  /** Base prefix for payment module configurations */
  const val PAY = "$ROOT.pay"

  /** WeChat Pay configuration prefix */
  const val PAY_WECHAT = "$PAY.wechat"

  // ========== PSDK Module Prefixes ==========

  /** Base prefix for platform SDK configurations */
  const val PSDK = "$ROOT.psdk"

  /** WeChat Platform API configuration prefix */
  const val PSDK_WXPA = "$PSDK.wxpa"

  // ========== Depend Module Prefixes ==========

  /** Base prefix for dependency module configurations */
  const val DEPEND = "$ROOT.depend"

  /** Servlet web application configuration prefix */
  const val DEPEND_SERVLET = "$DEPEND.servlet"

  /** PAHO MQTT client configuration prefix */
  const val DEPEND_PAHO = "$DEPEND.paho"

  /** PAHO MQTT client specific configuration prefix */
  const val DEPEND_PAHO_CLIENT = "$DEPEND_PAHO.client"

  /** SpringDoc OpenAPI configuration prefix */
  const val DEPEND_SPRINGDOC_OPENAPI = "$DEPEND.springdoc-open-api"

  // ========== TestToolkit Module Prefixes ==========

  /** Base prefix for test toolkit configurations */
  const val TESTTOOLKIT = "$ROOT.testtoolkit"

  /** Testcontainers configuration prefix */
  const val TESTTOOLKIT_TESTCONTAINERS = "$TESTTOOLKIT.testcontainers"

  // ========== Utility Methods ==========

  /**
   * Get all prefixes as a list for validation or documentation purposes.
   *
   * @return List of all configuration prefixes
   */
  fun getAllPrefixes(): List<String> =
    listOf(
      ROOT,
      SHARED,
      SHARED_SNOWFLAKE,
      SHARED_DATA_LOAD,
      SHARED_RESOURCE_HOLDER,
      SECURITY,
      SECURITY_JWT,
      SECURITY_KEYS,
      OSS,
      OSS_MINIO,
      OSS_VOLCENGINE_TOS,
      PAY,
      PAY_WECHAT,
      PSDK,
      PSDK_WXPA,
      DEPEND,
      DEPEND_SERVLET,
      DEPEND_PAHO,
      DEPEND_PAHO_CLIENT,
      DEPEND_SPRINGDOC_OPENAPI,
      TESTTOOLKIT,
      TESTTOOLKIT_TESTCONTAINERS,
    )

  /**
   * Get prefixes by module for organized access.
   *
   * @return Map of module name to list of prefixes
   */
  fun getPrefixesByModule(): Map<String, List<String>> =
    mapOf(
      "shared" to listOf(SHARED, SHARED_SNOWFLAKE, SHARED_DATA_LOAD, SHARED_RESOURCE_HOLDER),
      "security" to listOf(SECURITY, SECURITY_JWT, SECURITY_KEYS),
      "oss" to listOf(OSS, OSS_MINIO, OSS_VOLCENGINE_TOS),
      "pay" to listOf(PAY, PAY_WECHAT),
      "psdk" to listOf(PSDK, PSDK_WXPA),
      "depend" to listOf(DEPEND, DEPEND_SERVLET, DEPEND_PAHO, DEPEND_PAHO_CLIENT, DEPEND_SPRINGDOC_OPENAPI),
      "testtoolkit" to listOf(TESTTOOLKIT, TESTTOOLKIT_TESTCONTAINERS),
    )
}
