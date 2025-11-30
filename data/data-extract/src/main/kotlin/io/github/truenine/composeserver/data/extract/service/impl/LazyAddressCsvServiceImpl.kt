package io.github.truenine.composeserver.data.extract.service.impl

import io.github.truenine.composeserver.data.extract.domain.CnDistrictCode
import io.github.truenine.composeserver.data.extract.service.ILazyAddressService
import io.github.truenine.composeserver.holders.ResourceHolder
import io.github.truenine.composeserver.slf4j
import io.github.truenine.composeserver.string
import org.springframework.context.annotation.Primary
import org.springframework.core.io.Resource
import org.springframework.stereotype.Service
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap

private val log = slf4j<LazyAddressCsvServiceImpl>()

/**
 * Optimized CSV-based implementation of lazy address service with comprehensive backward traversal support.
 *
 * **Key Features:**
 * - **Backward/Reverse Retrieval:** Efficient parent-child relationship traversal using CnDistrictCode.back()
 * - **Performance Optimizations:** Indexed data structures for O(1) lookups instead of O(n) linear search
 * - **Lazy Loading:** Efficient caching strategy with memory-conscious data structures
 * - **Multi-version Support:** Year-based data versioning with automatic fallback capabilities
 * - **Streaming Processing:** Memory-efficient CSV processing for large datasets
 *
 * **Implementation Details:**
 * - Uses ConcurrentHashMap for thread-safe caching across multiple indices
 * - Implements indexed parent-child lookups for optimal backward traversal performance
 * - Supports dynamic CSV version management with cache invalidation
 * - Provides both recursive and traversal-based data access patterns
 *
 * @param resourceHolder Resource holder for accessing CSV files
 * @since 1.0.0
 */
@Primary
@Service
class LazyAddressCsvServiceImpl(private val resourceHolder: ResourceHolder) : ILazyAddressService {

  /**
   * CSV file definition with column mappings for administrative district data.
   *
   * @param fileName CSV file name containing district data
   * @param codeLine Column index for district code (default: 0)
   * @param nameLine Column index for district name (default: 1)
   * @param levelLine Column index for administrative level (default: 2)
   * @param parentCodeLine Column index for parent code used for backward traversal (default: 3)
   */
  data class CsvDefine(val fileName: String, val codeLine: Int = 0, val nameLine: Int = 1, val levelLine: Int = 2, val parentCodeLine: Int = 3)

  final val csvVersions: MutableMap<String, CsvDefine> = ConcurrentHashMap(16)

  // Optimized caching strategy with indexed data structures
  private val districtCache: ConcurrentMap<String, List<ILazyAddressService.CnDistrict>> = ConcurrentHashMap()
  private val districtIndexCache: ConcurrentMap<String, Map<String, ILazyAddressService.CnDistrict>> = ConcurrentHashMap()
  private val childrenIndexCache: ConcurrentMap<String, Map<String, List<ILazyAddressService.CnDistrict>>> = ConcurrentHashMap()

  override val logger
    get() = log

  final override val supportedDefaultYearVersion: String
    get() = "2024"

  init {
    val confinedCsvResources = resourceHolder.matchConfigResources("area_code*.csv")
    confinedCsvResources
      .mapNotNull { resource ->
        val yearVersion = "\\d{4}".toRegex().find(resource.filename ?: "")?.value
        yearVersion?.let { it to resource.filename!! }
      }
      .forEach { (year, filename) -> csvVersions[year] = CsvDefine(filename) }

    // Ensure default year version exists
    if (!csvVersions.containsKey(supportedDefaultYearVersion)) {
      csvVersions[supportedDefaultYearVersion] = CsvDefine("area_code_${supportedDefaultYearVersion}.csv")
    }
    log.debug("Configured CSV versions: {}", csvVersions)
  }

  /**
   * Removes support for a specific year version and clears all related caches. This ensures data consistency and prevents memory leaks.
   *
   * @param year Year version to remove from support
   */
  fun removeSupportedYear(year: String) {
    csvVersions.remove(year)
    clearCacheForYear(year)
  }

  operator fun plusAssign(definePair: Pair<String, CsvDefine>) {
    addSupportedYear(definePair.first, definePair.second)
  }

  operator fun minusAssign(yearKey: String) {
    removeSupportedYear(yearKey)
  }

  /**
   * Adds support for a new year version with CSV definition. Clears related caches to ensure data consistency.
   *
   * @param year Year version to add to supported versions
   * @param csvDefine CSV file definition for the new year version
   */
  fun addSupportedYear(year: String, csvDefine: CsvDefine) {
    csvVersions[year] = csvDefine
    clearCacheForYear(year) // Clear related caches to ensure data consistency
  }

  /**
   * Clears all cached data for a specific year to free memory and ensure data consistency.
   *
   * This method removes data from all three cache levels:
   * - District cache (raw data)
   * - District index cache (code-to-district mapping)
   * - Children index cache (parent-to-children mapping for backward traversal)
   *
   * @param year Year version to clear from all caches
   */
  private fun clearCacheForYear(year: String) {
    districtCache.remove(year)
    districtIndexCache.remove(year)
    childrenIndexCache.remove(year)
  }

  override val supportedYearVersions: List<String>
    get() = csvVersions.keys.toList()

  /**
   * Optimized implementation of fetchChildren with indexed lookups for efficient backward traversal.
   *
   * **Performance Features:**
   * - Uses indexed cache for O(1) parent-child lookups instead of O(n) filtering
   * - Lazy initialization of indices only when needed
   * - Early validation with fail-fast for invalid inputs
   * - Special handling for country-level queries with DEFAULT_COUNTRY_CODE
   *
   * **Backward Traversal Support:**
   * - Handles hierarchical parent-child relationships efficiently
   * - Supports normalized code lookup through CnDistrictCode object creation
   * - Enables reverse navigation from child to parent using district.code.back()
   *
   * @param parentCode Parent district code (supports both short and full formats)
   * @param yearVersion Data year version to search
   * @return List of direct child districts enabling backward traversal
   */
  override fun fetchChildren(parentCode: string, yearVersion: String): List<ILazyAddressService.CnDistrict> {
    log.debug("Finding children for parent code: {} in year: {}", parentCode, yearVersion)

    // Early validation - fail fast for invalid inputs
    if (parentCode.isBlank() || yearVersion.isBlank() || !csvVersions.containsKey(yearVersion)) {
      return emptyList()
    }

    // Get or build children index for this year
    val childrenIndex = childrenIndexCache.computeIfAbsent(yearVersion) { buildChildrenIndex(yearVersion) }

    // Handle special case for country-level query
    if (parentCode == ILazyAddressService.DEFAULT_COUNTRY_CODE) {
      return childrenIndex[parentCode] ?: emptyList()
    }

    // Create parent code object for normalization
    val parentCodeObj = ILazyAddressService.createCnDistrictCode(parentCode) ?: return emptyList()

    // Use indexed lookup for O(1) performance
    return childrenIndex[parentCodeObj.code] ?: emptyList()
  }

  /**
   * Optimized district lookup using indexed cache for O(1) performance with backward traversal support.
   *
   * **Features:**
   * - O(1) indexed lookup performance
   * - Code normalization through CnDistrictCode for consistent access
   * - Early validation with fail-fast error handling
   * - Returns districts with proper hierarchical information for backward navigation
   *
   * **Backward Traversal Integration:**
   * - Returned CnDistrict contains CnDistrictCode with back() method support
   * - Enables efficient parent lookup through district.code.back()
   * - Supports both partial and complete code formats
   *
   * @param code District code to find (supports various formats)
   * @param yearVersion Data year version to search
   * @return District object with backward traversal capabilities or null if not found
   */
  override fun fetchDistrict(code: string, yearVersion: String): ILazyAddressService.CnDistrict? {
    log.debug("Finding district for code: {} in year: {}", code, yearVersion)

    // Early validation
    if (code.isBlank() || yearVersion.isBlank() || !csvVersions.containsKey(yearVersion)) {
      return null
    }

    // Create code object for normalization
    val codeObj = ILazyAddressService.createCnDistrictCode(code) ?: return null

    // Get or build district index for O(1) lookup
    val districtIndex = districtIndexCache.computeIfAbsent(yearVersion) { buildDistrictIndex(yearVersion) }

    // Use indexed lookup for optimal performance
    return districtIndex[codeObj.code]
  }

  override fun fetchChildrenRecursive(parentCode: string, maxDepth: Int, yearVersion: String): List<ILazyAddressService.CnDistrict> {
    log.debug("Finding recursive children for code: {} with maxDepth: {} in year: {}", parentCode, maxDepth, yearVersion)

    // Return empty list if year version doesn't exist
    if (!csvVersions.containsKey(yearVersion)) {
      return emptyList()
    }

    if (maxDepth <= 0) return emptyList()

    val result = mutableListOf<ILazyAddressService.CnDistrict>()
    val queue = ArrayDeque<Pair<String, Int>>() // (code, remainingDepth)
    queue.add(parentCode to maxDepth)

    while (queue.isNotEmpty()) {
      val (currentCode, remainingDepth) = queue.removeFirst()
      if (remainingDepth <= 0) continue

      // Get direct children of current code
      val children = fetchChildren(currentCode, yearVersion)
      result.addAll(children)

      // Add children to queue for continued processing
      children.forEach { child -> queue.add(child.code.code to (remainingDepth - 1)) }
    }

    return result
  }

  override fun traverseChildrenRecursive(
    parentCode: string,
    maxDepth: Int,
    yearVersion: String,
    onVisit: (children: List<ILazyAddressService.CnDistrict>, depth: Int, parentDistrict: ILazyAddressService.CnDistrict?) -> Boolean,
  ) {
    fun walk(currentCode: String, depth: Int, parent: ILazyAddressService.CnDistrict?) {
      if (depth > maxDepth) return
      val children = fetchChildren(currentCode, yearVersion)
      if (children.isEmpty()) return
      val shouldContinue = onVisit(children, depth, parent)
      if (shouldContinue) {
        for (child in children) {
          if (!child.leaf) {
            walk(child.code.code, depth + 1, child)
          }
        }
      }
    }
    walk(parentCode, 1, null)
  }

  internal fun getCsvResource(yearVersion: String): Resource? {
    return csvVersions[yearVersion]?.let { resourceHolder.getConfigResource(it.fileName) }
  }

  /**
   * Optimized CSV parsing with error handling and performance improvements for backward traversal support.
   *
   * **Features:**
   * - Streaming CSV processing for memory efficiency
   * - Graceful error handling with logging for malformed data
   * - Line-by-line parsing with validation
   * - Creates CnDistrictCode objects that support backward navigation
   *
   * **Data Structure:**
   * - Parses CSV format: code,name,level,parentCode
   * - Creates CnDistrict objects with hierarchical information
   * - Enables backward traversal through proper CnDistrictCode instantiation
   *
   * @param yearVersion Year version to parse CSV data for
   * @return List of districts with backward traversal support or null if resource not found
   */
  internal fun getCsvSequence(yearVersion: String): List<ILazyAddressService.CnDistrict>? {
    return getCsvResource(yearVersion)?.let { resource ->
      try {
        resource.inputStream.bufferedReader().useLines { lines ->
          lines
            .filter { it.isNotBlank() }
            .mapNotNull { line ->
              try {
                line.split(',', limit = 4).let { parts ->
                  if (parts.size >= 3) {
                    ILazyAddressService.CnDistrict(code = CnDistrictCode(parts[0]), name = parts[1], yearVersion = yearVersion, level = parts[2].toInt())
                  } else null
                }
              } catch (e: Exception) {
                log.warn("Failed to parse CSV line: {} - {}", line, e.message)
                null
              }
            }
            .toList()
        }
      } catch (e: Exception) {
        log.error("Failed to read CSV resource for year: {}", yearVersion, e)
        null
      }
    }
  }

  /**
   * Builds optimized index for parent-child relationships enabling efficient backward traversal.
   *
   * **Core Function:** Creates HashMap-based index for O(1) parent-to-children lookups instead of O(n) filtering operations. This is essential for the
   * backward/reverse retrieval mechanism.
   *
   * **Backward Traversal Logic:**
   * - For level 1 (provinces): parent is DEFAULT_COUNTRY_CODE
   * - For other levels: uses district.code.back()?.code to determine parent
   * - Builds bidirectional relationship mapping for efficient traversal
   *
   * **Performance Benefits:**
   * - O(1) lookup time for finding children of any parent
   * - Memory-efficient with proper capacity planning
   * - Thread-safe using ConcurrentHashMap
   *
   * @param yearVersion Year version to build parent-child index for
   * @return Immutable map of parent codes to their direct children lists
   */
  private fun buildChildrenIndex(yearVersion: String): Map<String, List<ILazyAddressService.CnDistrict>> {
    val allDistricts = districtCache.computeIfAbsent(yearVersion) { getCsvSequence(yearVersion) ?: emptyList() }

    val childrenMap = HashMap<String, MutableList<ILazyAddressService.CnDistrict>>()

    // Build parent-child relationships
    for (district in allDistricts) {
      val parentCode =
        when (district.level) {
          1 -> ILazyAddressService.DEFAULT_COUNTRY_CODE
          else -> district.code.back()?.code ?: ILazyAddressService.DEFAULT_COUNTRY_CODE
        }

      childrenMap.computeIfAbsent(parentCode) { ArrayList() }.add(district)
    }

    // Convert to immutable map with immutable lists
    return childrenMap.mapValues { it.value.toList() }
  }

  /**
   * Builds optimized index for direct district code lookups supporting backward traversal.
   *
   * **Purpose:** Creates code-to-district mapping for O(1) district lookup performance. Essential for efficient backward traversal when navigating from child
   * to parent.
   *
   * **Integration with Backward Traversal:**
   * - Enables fast lookup of parent districts during backward navigation
   * - Supports CnDistrictCode.back() method for parent code resolution
   * - Provides foundation for recursive and traversal operations
   *
   * @param yearVersion Year version to build district index for
   * @return Immutable map of district codes to district objects with backward traversal capabilities
   */
  private fun buildDistrictIndex(yearVersion: String): Map<String, ILazyAddressService.CnDistrict> {
    val allDistricts = districtCache.computeIfAbsent(yearVersion) { getCsvSequence(yearVersion) ?: emptyList() }

    return allDistricts.associateBy { it.code.code }
  }
}
