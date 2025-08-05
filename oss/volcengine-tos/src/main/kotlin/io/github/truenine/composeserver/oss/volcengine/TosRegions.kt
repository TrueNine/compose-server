package io.github.truenine.composeserver.oss.volcengine

/**
 * Represents a Volcengine TOS region with its endpoints
 *
 * @param regionId The region identifier
 * @param chineseName The Chinese name of the region
 * @param internalEndpoint The internal network endpoint
 * @param externalEndpoint The external network endpoint
 * @param internalS3Endpoint The internal network S3-compatible endpoint
 * @param externalS3Endpoint The external network S3-compatible endpoint
 * @author TrueNine
 * @since 2025-08-04
 */
data class TosRegion(
  val regionId: String,
  val chineseName: String,
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

  /** 华北2（北京） */
  val CN_BEIJING =
    TosRegion(
      regionId = "cn-beijing",
      chineseName = "华北2（北京）",
      internalEndpoint = "tos-cn-beijing.ivolces.com",
      externalEndpoint = "tos-cn-beijing.volces.com",
      internalS3Endpoint = "tos-s3-cn-beijing.ivolces.com",
      externalS3Endpoint = "tos-s3-cn-beijing.volces.com",
    )

  /** 华南1（广州） */
  val CN_GUANGZHOU =
    TosRegion(
      regionId = "cn-guangzhou",
      chineseName = "华南1（广州）",
      internalEndpoint = "tos-cn-guangzhou.ivolces.com",
      externalEndpoint = "tos-cn-guangzhou.volces.com",
      internalS3Endpoint = "tos-s3-cn-guangzhou.ivolces.com",
      externalS3Endpoint = "tos-s3-cn-guangzhou.volces.com",
    )

  /** 华东2（上海） */
  val CN_SHANGHAI =
    TosRegion(
      regionId = "cn-shanghai",
      chineseName = "华东2（上海）",
      internalEndpoint = "tos-cn-shanghai.ivolces.com",
      externalEndpoint = "tos-cn-shanghai.volces.com",
      internalS3Endpoint = "tos-s3-cn-shanghai.ivolces.com",
      externalS3Endpoint = "tos-s3-cn-shanghai.volces.com",
    )

  /** 中国香港 */
  val CN_HONGKONG =
    TosRegion(
      regionId = "cn-hongkong",
      chineseName = "中国香港",
      internalEndpoint = "tos-cn-hongkong.ivolces.com",
      externalEndpoint = "tos-cn-hongkong.volces.com",
      internalS3Endpoint = "tos-s3-cn-hongkong.ivolces.com",
      externalS3Endpoint = "tos-s3-cn-hongkong.volces.com",
    )

  /** 亚太东南（柔佛） */
  val AP_SOUTHEAST_1 =
    TosRegion(
      regionId = "ap-southeast-1",
      chineseName = "亚太东南（柔佛）",
      internalEndpoint = "tos-ap-southeast-1.ivolces.com",
      externalEndpoint = "tos-ap-southeast-1.volces.com",
      internalS3Endpoint = "tos-s3-ap-southeast-1.ivolces.com",
      externalS3Endpoint = "tos-s3-ap-southeast-1.volces.com",
    )

  /** 亚太东南（雅加达） */
  val AP_SOUTHEAST_3 =
    TosRegion(
      regionId = "ap-southeast-3",
      chineseName = "亚太东南（雅加达）",
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
   * Find region by Chinese name
   *
   * @param chineseName The Chinese name to search for
   * @return The matching TosRegion or null if not found
   */
  fun findByChineseName(chineseName: String): TosRegion? {
    return ALL_REGIONS.find { it.chineseName == chineseName }
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
   * Get all Chinese names
   *
   * @return List of all Chinese region names
   */
  fun getAllChineseNames(): List<String> {
    return ALL_REGIONS.map { it.chineseName }
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
