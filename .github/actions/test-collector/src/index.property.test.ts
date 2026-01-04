/**
 * Property-based tests for Test Result Collection
 *
 * **Feature: github-actions-typescript, Property 7: Test Result Collection**
 * **Validates: Requirements 7.1, 7.4**
 */

import fc from 'fast-check'
import { describe, expect, it } from 'vitest'
import { generateTestSummary, parseJUnitXml } from './index.js'

/**
 * Generate a valid JUnit XML test case
 */
const junitTestCaseArb = fc.record({
  name: fc.string({ minLength: 1, maxLength: 50 }).map((s) => s.replace(/[<>&"']/g, '_')),
  classname: fc.string({ minLength: 1, maxLength: 100 }).map((s) => s.replace(/[<>&"']/g, '_')),
  time: fc.float({ min: 0, max: 100, noNaN: true }),
  failed: fc.boolean(),
  skipped: fc.boolean(),
})

/**
 * Generate a valid JUnit XML test suite
 */
const junitTestSuiteArb = fc.record({
  name: fc.string({ minLength: 1, maxLength: 50 }).map((s) => s.replace(/[<>&"']/g, '_')),
  testCases: fc.array(junitTestCaseArb, { minLength: 0, maxLength: 10 }),
})

/**
 * Convert test suite data to JUnit XML string
 */
function toJUnitXml(suite: {
  name: string
  testCases: Array<{
    name: string
    classname: string
    time: number
    failed: boolean
    skipped: boolean
  }>
}): string {
  const tests = suite.testCases.length
  const failures = suite.testCases.filter((tc) => tc.failed && !tc.skipped).length
  const skipped = suite.testCases.filter((tc) => tc.skipped).length
  const time = suite.testCases.reduce((sum, tc) => sum + tc.time, 0)

  let xml = `<?xml version="1.0" encoding="UTF-8"?>\n`
  xml += `<testsuite name="${suite.name}" tests="${tests}" failures="${failures}" errors="0" skipped="${skipped}" time="${time.toFixed(3)}">\n`

  for (const tc of suite.testCases) {
    xml += `  <testcase name="${tc.name}" classname="${tc.classname}" time="${tc.time.toFixed(3)}"`

    if (tc.skipped) {
      xml += `>\n    <skipped/>\n  </testcase>\n`
    } else if (tc.failed) {
      xml += `>\n    <failure message="Test failed" type="AssertionError">Stack trace here</failure>\n  </testcase>\n`
    } else {
      xml += `/>\n`
    }
  }

  xml += `</testsuite>\n`
  return xml
}

describe('property 7: Test Result Collection', () => {
  /**
   * Property: For any valid JUnit XML, parsing should extract correct test counts
   *
   * This validates Requirement 7.1: THE Test_Collector SHALL find all test result files
   * in build directories (by correctly parsing them)
   */
  it('should correctly parse test counts from JUnit XML', () => {
    fc.assert(
      fc.property(junitTestSuiteArb, (suiteData) => {
        const xml = toJUnitXml(suiteData)
        const parsed = parseJUnitXml(xml)

        // Total tests should match
        expect(parsed.tests).toBe(suiteData.testCases.length)

        // Failures should match (only non-skipped failures)
        const expectedFailures = suiteData.testCases.filter(
          (tc) => tc.failed && !tc.skipped,
        ).length
        expect(parsed.failures).toBe(expectedFailures)

        // Skipped should match
        const expectedSkipped = suiteData.testCases.filter((tc) => tc.skipped).length
        expect(parsed.skipped).toBe(expectedSkipped)

        // Suite name should match
        expect(parsed.name).toBe(suiteData.name)
      }),
      { numRuns: 100 },
    )
  })

  /**
   * Property: Parsed test cases should have correct structure
   */
  it('should parse test case details correctly', () => {
    fc.assert(
      fc.property(junitTestSuiteArb, (suiteData) => {
        const xml = toJUnitXml(suiteData)
        const parsed = parseJUnitXml(xml)

        // Number of parsed test cases should match
        expect(parsed.testCases.length).toBe(suiteData.testCases.length)

        // Each test case should have required fields
        for (const tc of parsed.testCases) {
          expect(typeof tc.name).toBe('string')
          expect(typeof tc.classname).toBe('string')
          expect(typeof tc.time).toBe('number')
          expect(tc.time).toBeGreaterThanOrEqual(0)
        }
      }),
      { numRuns: 100 },
    )
  })

  /**
   * Property: Failed tests should be identified with failure details
   *
   * This validates Requirement 7.4: WHEN tests fail, THE Test_Collector SHALL
   * provide troubleshooting suggestions
   */
  it('should identify failed tests with failure information', () => {
    fc.assert(
      fc.property(
        junitTestSuiteArb.filter((s) => s.testCases.some((tc) => tc.failed && !tc.skipped)),
        (suiteData) => {
          const xml = toJUnitXml(suiteData)
          const parsed = parseJUnitXml(xml)

          // Find failed test cases
          const failedCases = parsed.testCases.filter((tc) => tc.failure)

          // Should have at least one failed case
          expect(failedCases.length).toBeGreaterThan(0)

          // Each failed case should have failure details
          for (const tc of failedCases) {
            expect(tc.failure).toBeDefined()
            expect(typeof tc.failure?.message).toBe('string')
            expect(typeof tc.failure?.type).toBe('string')
          }
        },
      ),
      { numRuns: 100 },
    )
  })

  /**
   * Property: Skipped tests should be correctly identified
   */
  it('should identify skipped tests', () => {
    fc.assert(
      fc.property(
        junitTestSuiteArb.filter((s) => s.testCases.some((tc) => tc.skipped)),
        (suiteData) => {
          const xml = toJUnitXml(suiteData)
          const parsed = parseJUnitXml(xml)

          // Find skipped test cases
          const skippedCases = parsed.testCases.filter((tc) => tc.skipped)

          // Should have at least one skipped case
          expect(skippedCases.length).toBeGreaterThan(0)
        },
      ),
      { numRuns: 100 },
    )
  })

  /**
   * Property: Duration should be non-negative and sum correctly
   */
  it('should calculate duration correctly', () => {
    fc.assert(
      fc.property(junitTestSuiteArb, (suiteData) => {
        const xml = toJUnitXml(suiteData)
        const parsed = parseJUnitXml(xml)

        // Duration should be non-negative
        expect(parsed.time).toBeGreaterThanOrEqual(0)

        // Duration should approximately equal sum of test case times
        const expectedTime = suiteData.testCases.reduce((sum, tc) => sum + tc.time, 0)
        expect(parsed.time).toBeCloseTo(expectedTime, 2)
      }),
      { numRuns: 100 },
    )
  })

  /**
   * Property: Empty test suite should parse correctly
   */
  it('should handle empty test suites', () => {
    const emptySuite = {
      name: 'EmptySuite',
      testCases: [],
    }
    const xml = toJUnitXml(emptySuite)
    const parsed = parseJUnitXml(xml)

    expect(parsed.tests).toBe(0)
    expect(parsed.failures).toBe(0)
    expect(parsed.skipped).toBe(0)
    expect(parsed.testCases.length).toBe(0)
  })

  /**
   * Property: Summary generation should include all required information
   *
   * This validates Requirement 7.4: troubleshooting suggestions for failures
   */
  it('should generate summary with troubleshooting for failures', () => {
    fc.assert(
      fc.property(
        fc.record({
          groupName: fc.string({ minLength: 1, maxLength: 30 }).map((s) => s.replace(/\|/g, '_')),
          modules: fc.array(
            fc.string({ minLength: 1, maxLength: 20 }).map((s) => s.replace(/\|/g, '_')),
            { minLength: 1, maxLength: 5 },
          ),
          totalTests: fc.integer({ min: 1, max: 100 }),
          failedTests: fc.integer({ min: 1, max: 10 }),
          useTestContainers: fc.boolean(),
        }),
        ({ groupName, modules, totalTests, failedTests, useTestContainers }) => {
          const results = {
            totalTests,
            failedTests,
            errorTests: 0,
            skippedTests: 0,
            duration: 10.5,
            suites: [],
            failedCases: [
              {
                name: 'testMethod',
                classname: 'TestClass',
                time: 1.0,
                failure: {
                  message: 'Assertion failed',
                  type: 'AssertionError',
                  content: 'Stack trace',
                },
              },
            ],
          }

          const summary = generateTestSummary(groupName, modules, results, useTestContainers)

          // Summary should be a non-empty string
          expect(typeof summary).toBe('string')
          expect(summary.length).toBeGreaterThan(0)

          // Should contain the group name
          expect(summary).toContain(groupName)

          // Should contain test counts
          expect(summary).toContain(`${totalTests}`)

          // Should contain failure indicator for failed tests
          expect(summary).toContain('❌')

          // Should contain troubleshooting section for failures
          expect(summary).toContain('Troubleshooting')

          // If TestContainers used, should mention it
          if (useTestContainers) {
            expect(summary).toContain('TestContainers')
          }
        },
      ),
      { numRuns: 100 },
    )
  })

  /**
   * Property: Summary for passing tests should show success
   */
  it('should generate success summary for passing tests', () => {
    fc.assert(
      fc.property(
        fc.record({
          groupName: fc.string({ minLength: 1, maxLength: 30 }).map((s) => s.replace(/\|/g, '_')),
          modules: fc.array(
            fc.string({ minLength: 1, maxLength: 20 }).map((s) => s.replace(/\|/g, '_')),
            { minLength: 1, maxLength: 5 },
          ),
          totalTests: fc.integer({ min: 1, max: 100 }),
        }),
        ({ groupName, modules, totalTests }) => {
          const results = {
            totalTests,
            failedTests: 0,
            errorTests: 0,
            skippedTests: 0,
            duration: 10.5,
            suites: [],
            failedCases: [],
          }

          const summary = generateTestSummary(groupName, modules, results, false)

          // Should contain success indicator
          expect(summary).toContain('✅')

          // Should contain "Passed" status
          expect(summary).toContain('Passed')

          // Should NOT contain troubleshooting section
          expect(summary).not.toContain('Troubleshooting')
        },
      ),
      { numRuns: 100 },
    )
  })

  /**
   * Property: Malformed XML should be handled gracefully
   */
  it('should handle malformed XML gracefully', () => {
    const malformedInputs = [
      '',
      'not xml at all',
      '<testsuite>',
      '<testsuite name="test">',
      '<?xml version="1.0"?><root></root>',
    ]

    for (const input of malformedInputs) {
      // Should not throw
      const result = parseJUnitXml(input)

      // Should return a valid structure
      expect(typeof result.name).toBe('string')
      expect(typeof result.tests).toBe('number')
      expect(typeof result.failures).toBe('number')
      expect(Array.isArray(result.testCases)).toBe(true)
    }
  })
})
