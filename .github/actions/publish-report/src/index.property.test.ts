/**
 * Property-based tests for Report Format Consistency
 *
 * **Feature: github-actions-typescript, Property 8: Report Format Consistency**
 * **Validates: Requirements 8.4, 8.5**
 */

import type { ReportOptions, ReportType } from './index.js'
import fc from 'fast-check'
import { describe, expect, it } from 'vitest'
import {
  generateFailureReport,
  generateGradleDependency,
  generateMavenDependency,
  generatePrePublishReport,
  generateReport,
  generateSummaryReport,
  getMavenCentralUrl,
  getMavenSearchUrl,

} from './index.js'

/**
 * Generate a valid semver version string
 */
const semverArb = fc
  .tuple(
    fc.integer({ min: 0, max: 99 }),
    fc.integer({ min: 0, max: 99 }),
    fc.integer({ min: 0, max: 99 }),
  )
  .map(([major, minor, patch]) => `${major}.${minor}.${patch}`)

/**
 * Generate a prerelease version string
 */
const prereleaseVersionArb = fc
  .tuple(semverArb, fc.constantFrom('-SNAPSHOT', '-alpha.1', '-beta.2', '-rc.1'))
  .map(([version, suffix]) => `${version}${suffix}`)

/**
 * Generate any valid version (stable or prerelease)
 */
const versionArb = fc.oneof(semverArb, prereleaseVersionArb)

/**
 * Generate a valid Maven group ID
 */
const groupIdArb = fc
  .array(fc.stringMatching(/^[a-z][a-z0-9]*$/), { minLength: 2, maxLength: 4 })
  .map((parts) => parts.join('.'))

/**
 * Generate a valid artifact ID
 */
const artifactIdArb = fc
  .array(fc.stringMatching(/^[a-z][a-z0-9]*$/), { minLength: 1, maxLength: 3 })
  .map((parts) => parts.join('-'))

/**
 * Generate a list of artifacts
 */
const artifactsArb = fc.array(artifactIdArb, { minLength: 0, maxLength: 5 })

/**
 * Generate report options
 */
function reportOptionsArb(reportType: ReportType): fc.Arbitrary<ReportOptions> {
  return fc.record({
    reportType: fc.constant(reportType),
    version: versionArb,
    groupId: groupIdArb,
    artifacts: artifactsArb,
    publishSuccess: fc.boolean(),
    errorMessage: fc.string({ minLength: 0, maxLength: 200 }),
    errorDetails: fc.string({ minLength: 0, maxLength: 500 }),
    repositoryUrl: fc.oneof(
      fc.constant(''),
      fc.constant('https://github.com/owner/repo'),
    ),
    releaseUrl: fc.oneof(
      fc.constant(''),
      fc.constant('https://github.com/owner/repo/releases/tag/v1.0.0'),
    ),
  })
}

describe('property 8: Report Format Consistency', () => {
  /**
   * Property: All generated reports should be valid Markdown
   *
   * This validates Requirement 8.4: THE Report_Generator SHALL output reports
   * as GitHub Step Summary (which requires valid Markdown)
   */
  it('should generate valid Markdown for all report types', () => {
    const reportTypes: ReportType[] = ['pre-publish', 'summary', 'failure']

    for (const reportType of reportTypes) {
      fc.assert(
        fc.property(reportOptionsArb(reportType), (options) => {
          const report = generateReport(options)

          // Report should be a non-empty string
          expect(typeof report).toBe('string')
          expect(report.length).toBeGreaterThan(0)

          // Should start with a Markdown header
          expect(report).toMatch(/^## /)

          // Should contain proper Markdown table syntax if tables are present
          if (report.includes('|')) {
            // Tables should have header separator row
            expect(report).toMatch(/\| --- \|/)
          }

          // Should not have unclosed HTML tags (for collapsible sections)
          const detailsOpens = (report.match(/<details>/g) || []).length
          const detailsCloses = (report.match(/<\/details>/g) || []).length
          expect(detailsOpens).toBe(detailsCloses)

          const summaryOpens = (report.match(/<summary>/g) || []).length
          const summaryCloses = (report.match(/<\/summary>/g) || []).length
          expect(summaryOpens).toBe(summaryCloses)
        }),
        { numRuns: 100 },
      )
    }
  })

  /**
   * Property: Pre-publish reports should contain version and validation info
   */
  it('should include version info in pre-publish reports', () => {
    fc.assert(
      fc.property(reportOptionsArb('pre-publish'), (options) => {
        const report = generatePrePublishReport(options)

        // Should contain the version
        expect(report).toContain(options.version)

        // Should contain the group ID
        expect(report).toContain(options.groupId)

        // Should contain validation checklist
        expect(report).toContain('Validation Checklist')

        // Should contain version information section
        expect(report).toContain('Version Information')
      }),
      { numRuns: 100 },
    )
  })

  /**
   * Property: Summary reports should include Maven Central links when artifacts exist
   *
   * This validates Requirement 8.5: THE Report_Generator SHALL include links
   * to Maven Central and GitHub Release when applicable
   */
  it('should include Maven Central links in summary reports with artifacts', () => {
    fc.assert(
      fc.property(
        reportOptionsArb('summary').filter(
          (opts) => opts.artifacts.length > 0 && opts.publishSuccess,
        ),
        (options) => {
          const report = generateSummaryReport(options)

          // Should contain Maven Central reference
          expect(report.toLowerCase()).toContain('maven')

          // Should contain links section
          expect(report).toContain('Links')

          // Should contain usage examples
          expect(report).toContain('Usage Examples')
        },
      ),
      { numRuns: 100 },
    )
  })

  /**
   * Property: Summary reports should include GitHub Release link when provided
   */
  it('should include GitHub Release link when provided', () => {
    fc.assert(
      fc.property(
        reportOptionsArb('summary').filter(
          (opts) => opts.releaseUrl.length > 0 && opts.publishSuccess,
        ),
        (options) => {
          const report = generateSummaryReport(options)

          // Should contain the release URL
          expect(report).toContain(options.releaseUrl)

          // Should mention GitHub Release
          expect(report).toContain('Release')
        },
      ),
      { numRuns: 100 },
    )
  })

  /**
   * Property: Failure reports should contain error information and troubleshooting
   */
  it('should include error info and troubleshooting in failure reports', () => {
    fc.assert(
      fc.property(
        reportOptionsArb('failure').filter((opts) => opts.errorMessage.length > 0),
        (options) => {
          const report = generateFailureReport(options)

          // Should contain the error message
          expect(report).toContain(options.errorMessage)

          // Should contain troubleshooting section
          expect(report).toContain('Troubleshooting')

          // Should contain retry instructions
          expect(report).toContain('Retry')

          // Should indicate failure status
          expect(report).toContain('❌')
          expect(report).toContain('Failed')
        },
      ),
      { numRuns: 100 },
    )
  })

  /**
   * Property: Failure reports should include common error patterns
   */
  it('should include common error patterns in failure reports', () => {
    fc.assert(
      fc.property(reportOptionsArb('failure'), (options) => {
        const report = generateFailureReport(options)

        // Should contain common error patterns section
        expect(report).toContain('Common Error Patterns')

        // Should mention common issues
        expect(report).toContain('401')
        expect(report).toContain('403')
        expect(report).toContain('409')
      }),
      { numRuns: 100 },
    )
  })

  /**
   * Property: Maven Central URLs should be correctly formatted
   */
  it('should generate correct Maven Central URLs', () => {
    fc.assert(
      fc.property(groupIdArb, artifactIdArb, semverArb, (groupId, artifactId, version) => {
        const url = getMavenCentralUrl(groupId, artifactId, version)

        // Should be a valid URL
        expect(url).toMatch(/^https:\/\/repo1\.maven\.org\/maven2\//)

        // Should contain the artifact ID
        expect(url).toContain(artifactId)

        // Should contain the version
        expect(url).toContain(version)

        // Group ID dots should be converted to slashes
        const groupPath = groupId.replace(/\./g, '/')
        expect(url).toContain(groupPath)
      }),
      { numRuns: 100 },
    )
  })

  /**
   * Property: Maven search URLs should be correctly formatted
   */
  it('should generate correct Maven search URLs', () => {
    fc.assert(
      fc.property(groupIdArb, artifactIdArb, (groupId, artifactId) => {
        const url = getMavenSearchUrl(groupId, artifactId)

        // Should be a valid Sonatype URL
        expect(url).toMatch(/^https:\/\/central\.sonatype\.com\/artifact\//)

        // Should contain the group ID
        expect(url).toContain(groupId)

        // Should contain the artifact ID
        expect(url).toContain(artifactId)
      }),
      { numRuns: 100 },
    )
  })

  /**
   * Property: Maven dependency snippets should be valid XML
   */
  it('should generate valid Maven dependency XML', () => {
    fc.assert(
      fc.property(groupIdArb, artifactIdArb, semverArb, (groupId, artifactId, version) => {
        const xml = generateMavenDependency(groupId, artifactId, version)

        // Should contain dependency tags
        expect(xml).toContain('<dependency>')
        expect(xml).toContain('</dependency>')

        // Should contain all required elements
        expect(xml).toContain(`<groupId>${groupId}</groupId>`)
        expect(xml).toContain(`<artifactId>${artifactId}</artifactId>`)
        expect(xml).toContain(`<version>${version}</version>`)
      }),
      { numRuns: 100 },
    )
  })

  /**
   * Property: Gradle dependency snippets should be valid Kotlin DSL
   */
  it('should generate valid Gradle dependency Kotlin DSL', () => {
    fc.assert(
      fc.property(groupIdArb, artifactIdArb, semverArb, (groupId, artifactId, version) => {
        const gradle = generateGradleDependency(groupId, artifactId, version)

        // Should use implementation configuration
        expect(gradle).toContain('implementation(')

        // Should contain the full coordinate
        expect(gradle).toContain(`${groupId}:${artifactId}:${version}`)

        // Should be properly quoted
        expect(gradle).toMatch(/implementation\("[^"]+"\)/)
      }),
      { numRuns: 100 },
    )
  })

  /**
   * Property: Report type should be correctly identified in output
   */
  it('should identify report type correctly', () => {
    const reportTypes: ReportType[] = ['pre-publish', 'summary', 'failure']

    for (const reportType of reportTypes) {
      fc.assert(
        fc.property(reportOptionsArb(reportType), (options) => {
          const report = generateReport(options)

          // Each report type should have distinctive content
          switch (reportType) {
            case 'pre-publish':
              expect(report).toContain('Pre-Publish')
              expect(report).toContain('Validation')
              break
            case 'summary':
              expect(report).toContain('Summary')
              break
            case 'failure':
              expect(report).toContain('Failure')
              expect(report).toContain('Troubleshooting')
              break
          }
        }),
        { numRuns: 100 },
      )
    }
  })

  /**
   * Property: Prerelease versions should be flagged appropriately
   */
  it('should flag prerelease versions in pre-publish reports', () => {
    fc.assert(
      fc.property(
        reportOptionsArb('pre-publish').chain((opts) =>
          prereleaseVersionArb.map((version) => ({ ...opts, version })),
        ),
        (options) => {
          const report = generatePrePublishReport(options)

          // Should indicate prerelease status
          expect(report.toLowerCase()).toContain('prerelease')

          // Should have a warning
          expect(report).toContain('⚠️')
        },
      ),
      { numRuns: 100 },
    )
  })

  /**
   * Property: Stable versions should not be flagged as prerelease
   */
  it('should not flag stable versions as prerelease', () => {
    fc.assert(
      fc.property(
        reportOptionsArb('pre-publish').chain((opts) =>
          semverArb.map((version) => ({ ...opts, version })),
        ),
        (options) => {
          const report = generatePrePublishReport(options)

          // Should indicate stable release
          expect(report).toContain('stable release')
        },
      ),
      { numRuns: 100 },
    )
  })

  /**
   * Property: All artifacts should be listed in reports
   */
  it('should list all artifacts in pre-publish reports', () => {
    fc.assert(
      fc.property(
        reportOptionsArb('pre-publish').filter((opts) => opts.artifacts.length > 0),
        (options) => {
          const report = generatePrePublishReport(options)

          // Each artifact should be mentioned
          for (const artifact of options.artifacts) {
            expect(report).toContain(artifact)
          }

          // Should show artifact count
          expect(report).toContain(`${options.artifacts.length}`)
        },
      ),
      { numRuns: 100 },
    )
  })

  /**
   * Property: Error details should be included when provided
   */
  it('should include error details in failure reports when provided', () => {
    fc.assert(
      fc.property(
        reportOptionsArb('failure').filter((opts) => opts.errorDetails.length > 0),
        (options) => {
          const report = generateFailureReport(options)

          // Should contain the error details
          expect(report).toContain(options.errorDetails)

          // Should be in a collapsible section
          expect(report).toContain('<details>')
        },
      ),
      { numRuns: 100 },
    )
  })
})
