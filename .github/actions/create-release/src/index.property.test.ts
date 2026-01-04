/**
 * Property-based tests for Release Notes Generation
 *
 * **Feature: github-actions-typescript, Property 9: Release Notes Generation**
 * **Validates: Requirements 9.1, 9.2, 9.3**
 */

import fc from 'fast-check'
import { describe, expect, it } from 'vitest'
import {
  generateReleaseNotes,
  generateTagName,
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
  .tuple(semverArb, fc.constantFrom('-SNAPSHOT', '-alpha.1', '-beta.2', '-rc.1', '-M1'))
  .map(([version, suffix]) => `${version}${suffix}`)

/**
 * Generate any valid version (stable or prerelease)
 */
const versionArb = fc.oneof(semverArb, prereleaseVersionArb)

/**
 * Generate a version with optional 'v' prefix
 */
const versionWithPrefixArb = fc.oneof(
  versionArb,
  versionArb.map((v) => `v${v}`),
)

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

describe('property 9: Release Notes Generation', () => {
  /**
   * Property: Release notes should be generated from template
   *
   * This validates Requirement 9.1: THE Release_Creator SHALL generate
   * release notes from template
   */
  it('should generate release notes from template for all versions', () => {
    fc.assert(
      fc.property(versionArb, groupIdArb, artifactsArb, (version, groupId, artifacts) => {
        const notes = generateReleaseNotes(version, groupId, artifacts)

        // Notes should be a non-empty string
        expect(typeof notes).toBe('string')
        expect(notes.length).toBeGreaterThan(0)

        // Should start with a Markdown header
        expect(notes).toMatch(/^## /)

        // Should contain Maven Coordinates section
        expect(notes).toContain('Maven Coordinates')

        // Should contain Usage section
        expect(notes).toContain('Usage')

        // Should contain Gradle example
        expect(notes).toContain('Gradle')

        // Should contain Maven example
        expect(notes).toContain('Maven')
      }),
      { numRuns: 100 },
    )
  })

  /**
   * Property: Release notes should include Maven coordinates with correct version
   *
   * This validates Requirement 9.2: THE Release_Creator SHALL include
   * Maven coordinates in release notes
   */
  it('should include Maven coordinates with correct version', () => {
    fc.assert(
      fc.property(versionArb, groupIdArb, artifactsArb, (version, groupId, artifacts) => {
        const notes = generateReleaseNotes(version, groupId, artifacts)

        // Should contain the group ID
        expect(notes).toContain(groupId)

        // Should contain the version (without 'v' prefix)
        expect(notes).toContain(version)

        // Should contain proper Maven XML structure
        expect(notes).toContain('<groupId>')
        expect(notes).toContain('<artifactId>')
        expect(notes).toContain('<version>')

        // Should contain proper Gradle DSL structure
        expect(notes).toContain('implementation(')
      }),
      { numRuns: 100 },
    )
  })

  /**
   * Property: Prerelease versions should set prerelease flag
   *
   * This validates Requirement 9.3: THE Release_Creator SHALL set
   * appropriate release flags (draft, prerelease)
   */
  it('should flag prerelease versions appropriately in release notes', () => {
    fc.assert(
      fc.property(prereleaseVersionArb, groupIdArb, artifactsArb, (version, groupId, artifacts) => {
        const notes = generateReleaseNotes(version, groupId, artifacts)

        // Should indicate prerelease status
        expect(notes.toLowerCase()).toContain('pre-release')

        // Should have a warning indicator
        expect(notes).toContain('⚠️')

        // Should mention unstable features
        expect(notes.toLowerCase()).toContain('unstable')
      }),
      { numRuns: 100 },
    )
  })

  /**
   * Property: Stable versions should not be flagged as prerelease
   */
  it('should not flag stable versions as prerelease', () => {
    fc.assert(
      fc.property(semverArb, groupIdArb, artifactsArb, (version, groupId, artifacts) => {
        const notes = generateReleaseNotes(version, groupId, artifacts)

        // Should not have prerelease warning
        expect(notes).not.toContain('⚠️')

        // Should have release header without pre-release
        expect(notes).toMatch(/^## Release \d+\.\d+\.\d+/)
      }),
      { numRuns: 100 },
    )
  })

  /**
   * Property: All artifacts should be listed in release notes
   */
  it('should list all artifacts in release notes', () => {
    fc.assert(
      fc.property(
        versionArb,
        groupIdArb,
        artifactsArb.filter((a) => a.length > 0),
        (version, groupId, artifacts) => {
          const notes = generateReleaseNotes(version, groupId, artifacts)

          // Should contain Available Artifacts section
          expect(notes).toContain('Available Artifacts')

          // Each artifact should be mentioned
          for (const artifact of artifacts) {
            expect(notes).toContain(artifact)
          }

          // Should contain Maven Central links
          expect(notes).toContain('central.sonatype.com')
        },
      ),
      { numRuns: 100 },
    )
  })

  /**
   * Property: Tag name should be correctly generated
   */
  it('should generate correct tag name from version', () => {
    fc.assert(
      fc.property(versionWithPrefixArb, (version) => {
        const tagName = generateTagName(version)

        // Tag should start with 'v'
        expect(tagName).toMatch(/^v/)

        // Tag should not have double 'v' prefix
        expect(tagName).not.toMatch(/^vv/)

        // Tag should contain version numbers
        expect(tagName).toMatch(/v\d+\.\d+\.\d+/)
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
   * Property: Release notes should contain valid Markdown
   */
  it('should generate valid Markdown in release notes', () => {
    fc.assert(
      fc.property(versionArb, groupIdArb, artifactsArb, (version, groupId, artifacts) => {
        const notes = generateReleaseNotes(version, groupId, artifacts)

        // Should have proper Markdown table syntax if tables are present
        if (notes.includes('|') && notes.includes('Artifact')) {
          // Tables should have header separator row
          expect(notes).toMatch(/\| --- \|/)
        }

        // Should not have unclosed HTML tags (for collapsible sections)
        const detailsOpens = (notes.match(/<details>/g) || []).length
        const detailsCloses = (notes.match(/<\/details>/g) || []).length
        expect(detailsOpens).toBe(detailsCloses)

        const summaryOpens = (notes.match(/<summary>/g) || []).length
        const summaryCloses = (notes.match(/<\/summary>/g) || []).length
        expect(summaryOpens).toBe(summaryCloses)

        // Code blocks should be properly closed
        const codeBlockCount = (notes.match(/```/g) || []).length
        expect(codeBlockCount % 2).toBe(0)
      }),
      { numRuns: 100 },
    )
  })

  /**
   * Property: Usage examples should contain correct coordinates
   */
  it('should include correct coordinates in usage examples', () => {
    fc.assert(
      fc.property(
        versionArb,
        groupIdArb,
        artifactsArb.filter((a) => a.length > 0),
        (version, groupId, artifacts) => {
          const notes = generateReleaseNotes(version, groupId, artifacts)

          // Gradle example should have correct format
          expect(notes).toContain(`implementation("${groupId}:${artifacts[0]}:${version}")`)

          // Maven example should have correct elements
          expect(notes).toContain(`<groupId>${groupId}</groupId>`)
          expect(notes).toContain(`<artifactId>${artifacts[0]}</artifactId>`)
          expect(notes).toContain(`<version>${version}</version>`)
        },
      ),
      { numRuns: 100 },
    )
  })

  /**
   * Property: Empty artifacts list should use placeholder
   */
  it('should use placeholder when no artifacts provided', () => {
    fc.assert(
      fc.property(versionArb, groupIdArb, (version, groupId) => {
        const notes = generateReleaseNotes(version, groupId, [])

        // Should not contain Available Artifacts section
        expect(notes).not.toContain('Available Artifacts')

        // Should use placeholder in examples
        expect(notes).toContain('your-artifact')
      }),
      { numRuns: 100 },
    )
  })

  /**
   * Property: Version normalization should be consistent
   */
  it('should normalize versions consistently', () => {
    fc.assert(
      fc.property(semverArb, (version) => {
        const withPrefix = `v${version}`

        const notesWithPrefix = generateReleaseNotes(withPrefix, 'io.test', [])
        const notesWithoutPrefix = generateReleaseNotes(version, 'io.test', [])

        // Both should produce the same release notes
        expect(notesWithPrefix).toBe(notesWithoutPrefix)
      }),
      { numRuns: 100 },
    )
  })
})
