/**
 * Property-based tests for TOML Parser
 *
 * **Feature: github-actions-typescript, Property 2: TOML Parsing Round-Trip**
 * **Validates: Requirements 2.1, 2.5**
 */

import fc from 'fast-check'
import { describe, expect, it } from 'vitest'
import { extractVersionsFromToml, parseToml } from './toml-parser.js'
import { TomlParseError } from './types.js'

describe('property 2: TOML Parsing Round-Trip', () => {
  /**
   * Property: For any valid TOML content containing version fields,
   * parsing and extracting versions should either succeed with correct values
   * or fail with a descriptive error for malformed input.
   */
  it('should correctly parse valid version TOML content', () => {
    // Generate valid version strings (semver-like)
    const versionArb = fc.tuple(
      fc.integer({ min: 1, max: 99 }),
      fc.integer({ min: 0, max: 99 }),
      fc.integer({ min: 0, max: 99 }),
    ).map(([major, minor, patch]) => `${major}.${minor}.${patch}`)

    fc.assert(
      fc.property(
        versionArb,
        versionArb,
        versionArb,
        (javaVersion, gradleVersion, projectVersion) => {
          const tomlContent = `[versions]
java = "${javaVersion}"
org-gradle = "${gradleVersion}"
project = "${projectVersion}"
`
          const parsed = parseToml(tomlContent)
          const versions = extractVersionsFromToml(parsed)

          expect(versions.java).toBe(javaVersion)
          expect(versions.gradle).toBe(gradleVersion)
          expect(versions.project).toBe(projectVersion)
        },
      ),
      { numRuns: 100 },
    )
  })

  it('should throw TomlParseError for empty content', () => {
    fc.assert(
      fc.property(
        fc.constantFrom('', '   ', '\n', '\t', '  \n  '),
        (emptyContent) => {
          expect(() => parseToml(emptyContent)).toThrow(TomlParseError)
        },
      ),
      { numRuns: 100 },
    )
  })

  it('should throw TomlParseError for malformed TOML', () => {
    // Generate truly malformed TOML content
    const malformedArb = fc.oneof(
      // Missing closing quote
      fc.constant('[versions]\njava = "24'),
      // Missing equals sign
      fc.constant('[versions]\njava "24"'),
      // Unclosed section bracket
      fc.constant('[versions\njava = "24"'),
      // Invalid bare key with special characters
      fc.constant('[versions]\njava@version = "24"'),
      // Duplicate keys in same table (not allowed in TOML)
      fc.constant('[versions]\njava = "24"\njava = "25"'),
    )

    fc.assert(
      fc.property(malformedArb, (malformed) => {
        expect(() => parseToml(malformed)).toThrow(TomlParseError)
      }),
      { numRuns: 100 },
    )
  })

  it('should throw TomlParseError when required version fields are missing', () => {
    // Generate TOML with missing fields
    const missingFieldsArb = fc.oneof(
      // Missing java
      fc.constant('[versions]\norg-gradle = "9.0"\nproject = "1.0.0"'),
      // Missing org-gradle
      fc.constant('[versions]\njava = "24"\nproject = "1.0.0"'),
      // Missing project
      fc.constant('[versions]\njava = "24"\norg-gradle = "9.0"'),
      // Missing all
      fc.constant('[versions]\nother = "value"'),
      // Missing versions section
      fc.constant('[libraries]\nsome-lib = "1.0.0"'),
    )

    fc.assert(
      fc.property(missingFieldsArb, (tomlContent) => {
        const parsed = parseToml(tomlContent)
        expect(() => extractVersionsFromToml(parsed)).toThrow(TomlParseError)
      }),
      { numRuns: 100 },
    )
  })

  it('should handle version strings with various formats', () => {
    // Generate various valid version formats
    const versionFormatArb = fc.oneof(
      // Simple number
      fc.integer({ min: 1, max: 99 }).map(String),
      // Major.minor
      fc.tuple(
        fc.integer({ min: 1, max: 99 }),
        fc.integer({ min: 0, max: 99 }),
      ).map(([a, b]) => `${a}.${b}`),
      // Major.minor.patch
      fc.tuple(
        fc.integer({ min: 1, max: 99 }),
        fc.integer({ min: 0, max: 99 }),
        fc.integer({ min: 0, max: 99 }),
      ).map(([a, b, c]) => `${a}.${b}.${c}`),
      // Date-based version
      fc.tuple(
        fc.integer({ min: 2020, max: 2030 }),
        fc.integer({ min: 1, max: 12 }),
        fc.integer({ min: 1, max: 31 }),
      ).map(([y, m, d]) => `${y}.${m}.${d}`),
    )

    fc.assert(
      fc.property(
        versionFormatArb,
        versionFormatArb,
        versionFormatArb,
        (java, gradle, project) => {
          const tomlContent = `[versions]
java = "${java}"
org-gradle = "${gradle}"
project = "${project}"
`
          const parsed = parseToml(tomlContent)
          const versions = extractVersionsFromToml(parsed)

          expect(versions.java).toBe(java)
          expect(versions.gradle).toBe(gradle)
          expect(versions.project).toBe(project)
        },
      ),
      { numRuns: 100 },
    )
  })
})
