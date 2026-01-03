/**
 * Shared utilities for GitHub Actions
 *
 * This module exports all shared types and utility functions
 * used across custom GitHub Actions.
 */

// Export all types
export type {
    VersionInfo,
    CacheKeys,
    MavenCheckResult,
    TestGroup,
    GradleExecOptions,
    TestResult,
    ReleaseInfo,
} from './types.js';

// Export error classes
export { TomlParseError, MavenCentralError, GradleExecError } from './types.js';

// TOML Parser (Task 2.1)
export { parseToml, extractVersionsFromToml, extractVersions } from './toml-parser.js';

// Version Utils (Task 2.3)
export {
    isValidSemver,
    isPrerelease,
    parseVersion,
    compareSemver,
    extractVersionComponents,
} from './version-utils.js';

// GitHub Utils (Task 2.5)
export {
    setOutput,
    getInput,
    getBooleanInput,
    getMultilineInput,
    writeStepSummary,
    info,
    warning,
    error,
    setFailed,
    startGroup,
    endGroup,
    group,
    exportVariable,
    addPath,
    isDebug,
    debug,
    getGitHubContext,
    generateMarkdownTable,
    generateCollapsible,
} from './github-utils.js';

// Test Matrix (Task 2.6)
export {
    TEST_MATRIX,
    getTestGroup,
    getTestContainersGroups,
    getLightweightGroups,
    getTotalModuleCount,
    toGitHubMatrix,
} from './test-matrix.js';

// Cache Utils (Task 5)
export {
    hashFile,
    hashFiles,
    findFiles,
    fileExists,
    getOsPrefix,
    generateCacheKeys,
    generateCacheKeyFromPatterns,
    GRADLE_CACHE_PATTERNS,
} from './cache-utils.js';
