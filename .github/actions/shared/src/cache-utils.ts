/**
 * Cache utilities for generating consistent cache keys
 *
 * @module cache-utils
 */

import type { CacheKeys } from './types.js'
import { createHash } from 'node:crypto'
import { readFile, stat } from 'node:fs/promises'
import process from 'node:process'
import * as glob from '@actions/glob'

/**
 * Calculate SHA-256 hash of file contents
 *
 * @param filePath - Path to the file
 * @returns Hex-encoded hash string
 */
export async function hashFile(filePath: string): Promise<string> {
  const content = await readFile(filePath)
  return createHash('sha256').update(content).digest('hex')
}

/**
 * Calculate combined hash of multiple files
 *
 * @param filePaths - Array of file paths
 * @returns Hex-encoded combined hash string
 */
export async function hashFiles(filePaths: string[]): Promise<string> {
  const hash = createHash('sha256')

  // Sort paths for consistent ordering
  const sortedPaths = [...filePaths].sort()

  for (const filePath of sortedPaths) {
    try {
      const content = await readFile(filePath)
      // Include file path in hash for uniqueness
      hash.update(filePath)
      hash.update(content)
    } catch {
      // Skip files that don't exist or can't be read
      continue
    }
  }

  return hash.digest('hex')
}

/**
 * Find files matching glob patterns
 *
 * @param patterns - Glob patterns to match
 * @returns Array of matching file paths
 */
export async function findFiles(patterns: string[]): Promise<string[]> {
  const globber = await glob.create(patterns.join('\n'), {
    followSymbolicLinks: false,
  })
  return globber.glob()
}

/**
 * Check if a file exists
 *
 * @param filePath - Path to check
 * @returns true if file exists
 */
export async function fileExists(filePath: string): Promise<boolean> {
  try {
    await stat(filePath)
    return true
  } catch {
    return false
  }
}

/**
 * Get the runner OS prefix for cache keys
 *
 * @returns OS prefix string (e.g., "Linux", "Windows", "macOS")
 */
export function getOsPrefix(): string {
  const runnerOs = process.env.RUNNER_OS
  if (runnerOs) {
    return runnerOs
  }

  // Fallback for local development
  const platform = process.platform
  switch (platform) {
    case 'linux':
      return 'Linux'
    case 'win32':
      return 'Windows'
    case 'darwin':
      return 'macOS'
    default:
      return platform
  }
}

/**
 * Default file patterns for Gradle cache key generation
 */
export const GRADLE_CACHE_PATTERNS = {
  /** Gradle wrapper properties */
  wrapper: ['gradle/wrapper/gradle-wrapper.properties'],
  /** Version catalog */
  versionCatalog: ['gradle/libs.versions.toml'],
  /** Build logic files */
  buildLogic: ['build-logic/**/*.gradle.kts', 'build-logic/**/*.kt'],
  /** Root build files */
  rootBuild: ['build.gradle.kts', 'settings.gradle.kts', 'gradle.properties'],
}

/**
 * Generate cache keys for Gradle builds
 *
 * @param workspaceRoot - Root directory of the workspace (default: current directory)
 * @returns Cache keys object with gradle and deps cache keys
 */
export async function generateCacheKeys(workspaceRoot = '.'): Promise<CacheKeys> {
  const osPrefix = getOsPrefix()

  // Collect all files for gradle cache key
  const gradleCachePatterns = [
    ...GRADLE_CACHE_PATTERNS.wrapper,
    ...GRADLE_CACHE_PATTERNS.versionCatalog,
    ...GRADLE_CACHE_PATTERNS.buildLogic,
    ...GRADLE_CACHE_PATTERNS.rootBuild,
  ].map((p) => `${workspaceRoot}/${p}`)

  const gradleCacheFiles = await findFiles(gradleCachePatterns)
  const gradleCacheHash = await hashFiles(gradleCacheFiles)

  // Deps cache key is based on version catalog and wrapper only
  const depsCachePatterns = [...GRADLE_CACHE_PATTERNS.wrapper, ...GRADLE_CACHE_PATTERNS.versionCatalog].map(
    (p) => `${workspaceRoot}/${p}`,
  )

  const depsCacheFiles = await findFiles(depsCachePatterns)
  const depsCacheHash = await hashFiles(depsCacheFiles)

  // Truncate hash to 16 characters for readability
  const shortGradleHash = gradleCacheHash.substring(0, 16)
  const shortDepsHash = depsCacheHash.substring(0, 16)

  return {
    gradleCache: `${osPrefix}-gradle-${shortGradleHash}`,
    depsCache: `${osPrefix}-deps-${shortDepsHash}`,
  }
}

/**
 * Generate a cache key from specific file patterns
 *
 * @param patterns - Glob patterns for files to include
 * @param prefix - Prefix for the cache key
 * @returns Cache key string
 */
export async function generateCacheKeyFromPatterns(patterns: string[], prefix: string): Promise<string> {
  const osPrefix = getOsPrefix()
  const files = await findFiles(patterns)
  const hash = await hashFiles(files)
  const shortHash = hash.substring(0, 16)

  return `${osPrefix}-${prefix}-${shortHash}`
}
