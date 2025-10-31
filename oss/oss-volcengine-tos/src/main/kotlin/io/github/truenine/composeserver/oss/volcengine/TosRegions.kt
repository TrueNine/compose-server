package io.github.truenine.composeserver.oss.volcengine

/**
 * Represents a Volcengine TOS region with its endpoints
 *
 * @param regionId The region identifier
 * @param regionName The name of the region
 * @param internalEndpoint The internal network endpoint
 * @param externalEndpoint The external network endpoint
 * @param internalS3Endpoint The internal network S3-compatible endpoint
 * @param externalS3Endpoint The external network S3-compatible endpoint
 * @author TrueNine
 * @since 2025-08-04
 */
data class TosRegion(
  val regionId: String,
  val regionName: String,
  val internalEndpoint: String,
  val externalEndpoint: String,
  val internalS3Endpoint: String,
  val externalS3Endpoint: String,
) {
  /**
   * Get the appropriate endpoint based on network type
   *
   * @param useInternal Whether to use internal network endpoint
   * @param useS3Compatible Whether to use S3-compatible endpoint
   * @return The appropriate endpoint URL
   */
  fun getEndpoint(useInternal: Boolean = false, useS3Compatible: Boolean = false): String {
    return when {
      useInternal && useS3Compatible -> internalS3Endpoint
      useInternal && !useS3Compatible -> internalEndpoint
      !useInternal && useS3Compatible -> externalS3Endpoint
      else -> externalEndpoint
    }
  }
}

/**
 * Volcengine TOS region constants
 *
 * Contains all available TOS regions with their endpoints and metadata. Provides convenient access to region information and lookup methods.
 *
 * Region naming follows AWS-compatible standards:
 * - China regions: cn-{city} (e.g., cn-beijing)
 * - Asia Pacific regions: ap-{direction}-{number} (e.g., ap-southeast-1)
 *
 * @author TrueNine
 * @since 2025-08-04
 */
object TosRegions {

  /** North China 2 (Beijing) */
  val CN_BEIJING =
    TosRegion(
      regionId = "cn-beijing",
      regionName = "North China 2 (Beijing)",
      internalEndpoint = "tos-cn-beijing.ivolces.com",
      externalEndpoint = "tos-cn-beijing.volces.com",
      internalS3Endpoint = "tos-s3-cn-beijing.ivolces.com",
      externalS3Endpoint = "tos-s3-cn-beijing.volces.com",
    )

  /** South China 1 (Guangzhou) */
  val CN_GUANGZHOU =
    TosRegion(
      regionId = "cn-guangzhou",
      regionName = "South China 1 (Guangzhou)",
      internalEndpoint = "tos-cn-guangzhou.ivolces.com",
      externalEndpoint = "tos-cn-guangzhou.volces.com",
      internalS3Endpoint = "tos-s3-cn-guangzhou.ivolces.com",
      externalS3Endpoint = "tos-s3-cn-guangzhou.volces.com",
    )

  /** East China 2 (Shanghai) */
  val CN_SHANGHAI =
    TosRegion(
      regionId = "cn-shanghai",
      regionName = "East China 2 (Shanghai)",
      internalEndpoint = "tos-cn-shanghai.ivolces.com",
      externalEndpoint = "tos-cn-shanghai.volces.com",
      internalS3Endpoint = "tos-s3-cn-shanghai.ivolces.com",
      externalS3Endpoint = "tos-s3-cn-shanghai.volces.com",
    )

  /** China (Hong Kong) */
  val CN_HONGKONG =
    TosRegion(
      regionId = "cn-hongkong",
      regionName = "China (Hong Kong)",
      internalEndpoint = "tos-cn-hongkong.ivolces.com",
      externalEndpoint = "tos-cn-hongkong.volces.com",
      internalS3Endpoint = "tos-s3-cn-hongkong.ivolces.com",
      externalS3Endpoint = "tos-s3-cn-hongkong.volces.com",
    )

  /** Asia Pacific SE 1 (Johor) */
  val AP_SOUTHEAST_1 =
    TosRegion(
      regionId = "ap-southeast-1",
      regionName = "Asia Pacific SE 1 (Johor)",
      internalEndpoint = "tos-ap-southeast-1.ivolces.com",
      externalEndpoint = "tos-ap-southeast-1.volces.com",
      internalS3Endpoint = "tos-s3-ap-southeast-1.ivolces.com",
      externalS3Endpoint = "tos-s3-ap-southeast-1.volces.com",
    )

  /** Asia Pacific SE 3 (Jakarta) */
  val AP_SOUTHEAST_3 =
    TosRegion(
      regionId = "ap-southeast-3",
      regionName = "Asia Pacific SE 3 (Jakarta)",
      internalEndpoint = "tos-ap-southeast-3.ivolces.com",
      externalEndpoint = "tos-ap-southeast-3.volces.com",
      internalS3Endpoint = "tos-s3-ap-southeast-3.ivolces.com",
      externalS3Endpoint = "tos-s3-ap-southeast-3.volces.com",
    )

  /** All available regions */
  val ALL_REGIONS = listOf(CN_BEIJING, CN_GUANGZHOU, CN_SHANGHAI, CN_HONGKONG, AP_SOUTHEAST_1, AP_SOUTHEAST_3)

  /**
   * Find region by region ID
   *
   * @param regionId The region identifier to search for
   * @return The matching TosRegion or null if not found
   */
  fun findByRegionId(regionId: String): TosRegion? {
    return ALL_REGIONS.find { it.regionId == regionId }
  }

  /**
   * Find region by name
   *
   * @param regionName The region name to search for
   * @return The matching TosRegion or null if not found
   */
  fun findByRegionName(regionName: String): TosRegion? {
    return ALL_REGIONS.find { it.regionName == regionName }
  }

  /**
   * Get all region IDs
   *
   * @return List of all region identifiers
   */
  fun getAllRegionIds(): List<String> {
    return ALL_REGIONS.map { it.regionId }
  }

  /**
   * Get all region names
   *
   * @return List of all region names
   */
  fun getAllRegionNames(): List<String> {
    return ALL_REGIONS.map { it.regionName }
  }

  /**
   * Check if a region ID is valid
   *
   * @param regionId The region identifier to validate
   * @return True if the region ID exists, false otherwise
   */
  fun isValidRegionId(regionId: String): Boolean {
    return findByRegionId(regionId) != null
  }
}
