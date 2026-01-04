/**
 * Property-based tests for Cache Utils
 *
 * **Feature: github-actions-typescript, Property 3: Cache Key Consistency**
 * **Validates: Requirements 3.1, 3.5**
 */

import { mkdir, rm, writeFile } from 'node:fs/promises'
import { tmpdir } from 'node:os'
import { join } from 'node:path'
import fc from 'fast-check'
import { afterEach, beforeEach, describe, expect, it } from 'vitest'
import { generateCacheKeyFromPatterns, getOsPrefix, hashFile, hashFiles } from './cache-utils.js'

describe('property 3: Cache Key Consistency', () => {
  let testDir: string

  beforeEach(async () => {
    testDir = join(tmpdir(), `cache-test-${Date.now()}-${Math.random().toString(36).slice(2)}`)
    await mkdir(testDir, { recursive: true })
  })

  afterEach(async () => {
    try {
      await rm(testDir, { recursive: true, force: true })
    } catch {
      // Ignore cleanup errors
    }
  })

  /**
   * Property: Cache keys should be prefixed with the runner OS
   */
  it('should prefix cache keys with runner OS', () => {
    const runnerOsArb = fc.option(fc.constantFrom('Linux', 'Windows', 'macOS'), { nil: null })
    fc.assert(
      fc.property(
        runnerOsArb,
        (runnerOs) => {
          const originalEnv = process.env.RUNNER_OS
          try {
            if (typeof runnerOs === 'string') {
              process.env.RUNNER_OS = runnerOs
            } else {
              delete process.env.RUNNER_OS
            }

            const osPrefix = getOsPrefix()

            // Should return a non-empty string
            expect(osPrefix.length).toBeGreaterThan(0)

            // If RUNNER_OS is set, should return it
            if (typeof runnerOs === 'string') {
              expect(osPrefix).toBe(runnerOs)
            }
          } finally {
            if (typeof originalEnv === 'string') {
              process.env.RUNNER_OS = originalEnv
            } else {
              delete process.env.RUNNER_OS
            }
          }
        },
      ),
      { numRuns: 100 },
    )
  })

  /**
   * Property: Same file content should produce same hash
   */
  it('should produce consistent hash for same content', async () => {
    await fc.assert(
      fc.asyncProperty(
        fc.string({ minLength: 1, maxLength: 1000 }),
        async (content) => {
          const filePath = join(testDir, 'test-file.txt')
          await writeFile(filePath, content)

          const hash1 = await hashFile(filePath)
          const hash2 = await hashFile(filePath)

          expect(hash1).toBe(hash2)
          // SHA-256 hashes are 64 hex characters long
          expect(hash1.length).toBe(64)
        },
      ),
      { numRuns: 100 },
    )
  })

  /**
   * Property: Different content should produce different hash
   */
  it('should produce different hash for different content', async () => {
    await fc.assert(
      fc.asyncProperty(
        fc.string({ minLength: 1, maxLength: 500 }),
        fc.string({ minLength: 1, maxLength: 500 }),
        async (content1, content2) => {
          // Skip if contents are the same
          fc.pre(content1 !== content2)

          const file1 = join(testDir, 'file1.txt')
          const file2 = join(testDir, 'file2.txt')

          await writeFile(file1, content1)
          await writeFile(file2, content2)

          const hash1 = await hashFile(file1)
          const hash2 = await hashFile(file2)

          expect(hash1).not.toBe(hash2)
        },
      ),
      { numRuns: 100 },
    )
  })

  /**
   * Property: Hash of multiple files should be consistent regardless of call order
   */
  it('should produce consistent hash for multiple files', async () => {
    await fc.assert(
      fc.asyncProperty(
        fc.array(fc.string({ minLength: 1, maxLength: 200 }), { minLength: 1, maxLength: 5 }),
        async (contents) => {
          const files: string[] = []

          for (let i = 0; i < contents.length; i++) {
            const filePath = join(testDir, `file-${i}.txt`)
            await writeFile(filePath, contents[i])
            files.push(filePath)
          }

          const hash1 = await hashFiles(files)
          const hash2 = await hashFiles(files)

          expect(hash1).toBe(hash2)
        },
      ),
      { numRuns: 100 },
    )
  })

  /**
   * Property: Hash should change when any included file changes
   */
  it('should change hash when file content changes', async () => {
    await fc.assert(
      fc.asyncProperty(
        fc.string({ minLength: 1, maxLength: 200 }),
        fc.string({ minLength: 1, maxLength: 200 }),
        async (content1, content2) => {
          // Skip if contents are the same
          fc.pre(content1 !== content2)

          const filePath = join(testDir, 'changing-file.txt')

          await writeFile(filePath, content1)
          const hash1 = await hashFiles([filePath])

          await writeFile(filePath, content2)
          const hash2 = await hashFiles([filePath])

          expect(hash1).not.toBe(hash2)
        },
      ),
      { numRuns: 100 },
    )
  })

  /**
   * Property: Cache key format should be consistent
   */
  it('should generate cache key with correct format', async () => {
    await fc.assert(
      fc.asyncProperty(
        fc.string({ minLength: 1, maxLength: 100 }),
        fc.constantFrom('gradle', 'deps', 'build', 'test'),
        async (content, prefix) => {
          const filePath = join(testDir, 'format-test.txt')
          await writeFile(filePath, content)

          const cacheKey = await generateCacheKeyFromPatterns([filePath], prefix)

          // Should match format: {OS}-{prefix}-{hash}
          const parts = cacheKey.split('-')
          expect(parts.length).toBeGreaterThanOrEqual(3)

          // First part should be OS
          const osPrefix = getOsPrefix()
          expect(cacheKey.startsWith(osPrefix)).toBe(true)

          // Should contain the prefix
          expect(cacheKey).toContain(prefix)

          // Hash part should be 16 characters (truncated)
          const hashPart = parts[parts.length - 1]
          expect(hashPart.length).toBe(16)
        },
      ),
      { numRuns: 100 },
    )
  })

  /**
   * Property: Empty file list should produce consistent hash
   */
  it('should handle empty file list consistently', async () => {
    const hash1 = await hashFiles([])
    const hash2 = await hashFiles([])

    expect(hash1).toBe(hash2)
    expect(hash1.length).toBe(64)
  })

  /**
   * Property: Non-existent files should be skipped without error
   */
  it('should skip non-existent files gracefully', async () => {
    await fc.assert(
      fc.asyncProperty(
        fc.string({ minLength: 1, maxLength: 100 }),
        async (content) => {
          const existingFile = join(testDir, 'existing.txt')
          const nonExistentFile = join(testDir, 'non-existent-file.txt')

          await writeFile(existingFile, content)

          // Should not throw
          const hash = await hashFiles([existingFile, nonExistentFile])
          expect(hash.length).toBe(64)

          // Hash should be same as just the existing file
          const hashExistingOnly = await hashFiles([existingFile])
          expect(hash).toBe(hashExistingOnly)
        },
      ),
      { numRuns: 100 },
    )
  })
})
