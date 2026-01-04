/**
 * Version information extracted from libs.versions.toml
 */
export interface VersionInfo {
  /** Java version (e.g., "24") */
  java: string
  /** Gradle version (e.g., "9.0") */
  gradle: string
  /** Project version (e.g., "0.3.0") */
  project: string
}

/**
 * Cache keys for Gradle builds
 */
export interface CacheKeys {
  /** Key for Gradle build cache */
  gradleCache: string
  /** Key for dependencies cache */
  depsCache: string
}

/**
 * Result of Maven Central version check
 */
export interface MavenCheckResult {
  /** Version being checked */
  version: string
  /** Whether version is a snapshot/prerelease */
  isSnapshot: boolean
  /** Whether to proceed with publishing */
  shouldPublish: boolean
  /** Whether version already exists on Maven Central */
  versionExistsOnCentral: boolean
}

/**
 * Test group configuration for parallel test execution
 */
export interface TestGroup {
  /** Unique name for the test group */
  name: string
  /** List of modules to test */
  modules: string[]
  /** Whether TestContainers is required */
  testcontainers: boolean
  /** Timeout in minutes */
  timeout: number
}

/**
 * Options for Gradle command execution
 */
export interface GradleExecOptions {
  /** Gradle tasks to execute */
  tasks: string[]
  /** Enable parallel execution */
  parallel?: boolean
  /** Enable build cache */
  buildCache?: boolean
  /** Enable configuration cache */
  configurationCache?: boolean
  /** Maximum number of workers */
  maxWorkers?: number
  /** JVM arguments */
  jvmArgs?: string
  /** Timeout in minutes */
  timeout?: number
}

/**
 * Result of test execution
 */
export interface TestResult {
  /** Test group name */
  group: string
  /** Whether all tests passed */
  passed: boolean
  /** Total number of tests */
  totalTests: number
  /** Number of failed tests */
  failedTests: number
  /** Number of skipped tests */
  skippedTests: number
  /** Duration in seconds */
  duration: number
}

/**
 * Information for creating a GitHub release
 */
export interface ReleaseInfo {
  /** Version being released */
  version: string
  /** Whether this is a prerelease */
  isPrerelease: boolean
  /** Generated release notes */
  releaseNotes: string
  /** Git tag name */
  tagName: string
}

/**
 * Error thrown when TOML parsing fails
 */
export class TomlParseError extends Error {
  constructor(
    message: string,
    public readonly line?: number,
    public readonly column?: number,
  ) {
    const lineInfo = typeof line === 'number' ? ` at line ${line}` : ''
    super(`TOML Parse Error: ${message}${lineInfo}`)
    this.name = 'TomlParseError'
  }
}

/**
 * Error thrown when Maven Central operations fail
 */
export interface MavenCentralErrorDetails {
  statusCode?: number
  retryCount?: number
}

export class MavenCentralError extends Error {
  public readonly statusCode?: number
  public readonly retryCount?: number

  constructor(
    message: string,
    public readonly artifact: string,
    details: MavenCentralErrorDetails = {},
  ) {
    super(`Maven Central Error for ${artifact}: ${message}`)
    this.name = 'MavenCentralError'
    this.statusCode = details.statusCode
    this.retryCount = details.retryCount
  }
}

/**
 * Error thrown when Gradle execution fails
 */
export class GradleExecError extends Error {
  constructor(
    message: string,
    public readonly exitCode: number,
    public readonly stdout: string,
    public readonly stderr: string,
  ) {
    super(`Gradle Execution Failed (exit ${exitCode}): ${message}`)
    this.name = 'GradleExecError'
  }
}
