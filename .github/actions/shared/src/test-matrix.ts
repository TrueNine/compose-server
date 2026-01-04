/**
 * Test Matrix Configuration
 *
 * Defines test groups for parallel test execution in CI.
 * Each group contains related modules that can be tested together.
 *
 * @module test-matrix
 */

import type { TestGroup } from './types.js'

/**
 * Test matrix configuration with 12 test groups
 *
 * Groups are organized by:
 * - Functional domain (core, rds, business, security, etc.)
 * - TestContainers requirement (affects CI resource allocation)
 * - Expected execution time (timeout configuration)
 */
export const TEST_MATRIX: TestGroup[] = [
  {
    name: 'core-foundation',
    modules: ['shared', 'cacheable', 'docsite'],
    testcontainers: false,
    timeout: 8,
  },
  {
    name: 'core-build-tools',
    modules: ['gradleplugin:gradleplugin-composeserver', 'version-catalog', 'bom'],
    testcontainers: false,
    timeout: 10,
  },
  {
    name: 'rds-light',
    modules: ['rds:rds-shared', 'rds:rds-flyway-migration-shared'],
    testcontainers: false,
    timeout: 8,
  },
  {
    name: 'rds-heavy',
    modules: [
      'rds:rds-crud',
      'rds:rds-jimmer-ext-postgres',
      'rds:rds-flyway-migration-postgresql',
      'rds:rds-flyway-migration-mysql8',
    ],
    testcontainers: true,
    timeout: 25,
  },
  {
    name: 'business-ai-pay',
    modules: ['ai:ai-shared', 'ai:ai-langchain4j', 'pay:pay-shared', 'pay:pay-wechat'],
    testcontainers: true,
    timeout: 18,
  },
  {
    name: 'business-oss',
    modules: [
      'oss:oss-shared',
      'oss:oss-minio',
      'oss:oss-aliyun-oss',
      'oss:oss-huawei-obs',
      'oss:oss-volcengine-tos',
    ],
    testcontainers: true,
    timeout: 20,
  },
  {
    name: 'business-communication',
    modules: [
      'sms:sms-shared',
      'sms:sms-tencent',
      'surveillance:surveillance-shared',
      'surveillance:surveillance-hikvision',
    ],
    testcontainers: true,
    timeout: 15,
  },
  {
    name: 'security',
    modules: ['security:security-crypto', 'security:security-oauth2', 'security:security-spring'],
    testcontainers: false,
    timeout: 12,
  },
  {
    name: 'data-processing',
    modules: ['data:data-crawler', 'data:data-extract'],
    testcontainers: true,
    timeout: 15,
  },
  {
    name: 'platform-integrations',
    modules: [
      'depend:depend-http-exchange',
      'depend:depend-jackson',
      'depend:depend-paho',
      'depend:depend-servlet',
      'depend:depend-springdoc-openapi',
      'depend:depend-xxl-job',
      'ksp:ksp-meta',
      'ksp:ksp-plugin',
      'ksp:ksp-shared',
      'psdk:psdk-wxpa',
      'ide:ide-idea-mcp',
    ],
    testcontainers: false,
    timeout: 18,
  },
  {
    name: 'testing-tools',
    modules: [
      'testtoolkit:testtoolkit-shared',
      'testtoolkit:testtoolkit-testcontainers',
      'testtoolkit:testtoolkit-springmvc',
    ],
    testcontainers: true,
    timeout: 15,
  },
  {
    name: 'integration-tests',
    modules: [
      'integrate-test:depend:jackson',
      'integrate-test:oss:minio',
      'integrate-test:cacheable',
    ],
    testcontainers: true,
    timeout: 30,
  },
]

/**
 * Get a test group by name
 *
 * @param name - Test group name
 * @returns TestGroup or undefined if not found
 */
export function getTestGroup(name: string): TestGroup | undefined {
  return TEST_MATRIX.find((group) => group.name === name)
}

/**
 * Get all test groups that require TestContainers
 *
 * @returns Array of TestGroups that need TestContainers
 */
export function getTestContainersGroups(): TestGroup[] {
  return TEST_MATRIX.filter((group) => group.testcontainers)
}

/**
 * Get all test groups that don't require TestContainers
 *
 * @returns Array of TestGroups that don't need TestContainers
 */
export function getLightweightGroups(): TestGroup[] {
  return TEST_MATRIX.filter((group) => !group.testcontainers)
}

/**
 * Get the total number of modules across all test groups
 *
 * @returns Total module count
 */
export function getTotalModuleCount(): number {
  return TEST_MATRIX.reduce((sum, group) => sum + group.modules.length, 0)
}

/**
 * Convert test matrix to GitHub Actions matrix format
 *
 * @returns Object suitable for GitHub Actions matrix strategy
 */
export function toGitHubMatrix(): { include: Array<{ name: string, modules: string, testcontainers: boolean, timeout: number }> } {
  return {
    include: TEST_MATRIX.map((group) => ({
      name: group.name,
      modules: group.modules.join(' '),
      testcontainers: group.testcontainers,
      timeout: group.timeout,
    })),
  }
}
