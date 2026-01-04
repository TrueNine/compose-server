/**
 * Shared utilities for GitHub Actions
 *
 * This module exports all shared types and utility functions
 * used across custom GitHub Actions.
 */

// Cache Utils (Task 5)
export {
  fileExists,
  findFiles,
  generateCacheKeyFromPatterns,
  generateCacheKeys,
  getOsPrefix,
  GRADLE_CACHE_PATTERNS,
  hashFile,
  hashFiles,
} from './cache-utils.js'

// GitHub Utils (Task 2.5)
export {
  addPath,
  debug,
  endGroup,
  error,
  exportVariable,
  generateCollapsible,
  generateMarkdownTable,
  getBooleanInput,
  getGitHubContext,
  getInput,
  getMultilineInput,
  group,
  info,
  isDebug,
  setFailed,
  setOutput,
  startGroup,
  warning,
  writeStepSummary,
} from './github-utils.js'

// Test Matrix (Task 2.6)
export {
  getLightweightGroups,
  getTestContainersGroups,
  getTestGroup,
  getTotalModuleCount,
  TEST_MATRIX,
  toGitHubMatrix,
} from './test-matrix.js'

// TOML Parser (Task 2.1)
export { extractVersions, extractVersionsFromToml, parseToml } from './toml-parser.js'

// Export all types
export type {
  CacheKeys,
  GradleExecOptions,
  MavenCheckResult,
  ReleaseInfo,
  TestGroup,
  TestResult,
  VersionInfo,
} from './types.js'

// Export error classes
export { GradleExecError, MavenCentralError, TomlParseError } from './types.js'

// Version Utils (Task 2.3)
export {
  compareSemver,
  extractVersionComponents,
  isPrerelease,
  isValidSemver,
  parseVersion,
} from './version-utils.js'
