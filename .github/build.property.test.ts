/**
 * Property-based tests for Build Output
 *
 * **Feature: github-actions-typescript, Property 1: Build Output Single File**
 * **Validates: Requirements 1.4**
 */

import { execSync } from 'node:child_process'
import * as fs from 'node:fs'
import * as path from 'node:path'
import fc from 'fast-check'
import { beforeAll, describe, expect, it } from 'vitest'

const actionsDir = path.join(import.meta.dirname, 'actions')

/**
 * Get all action directories that should have dist output
 */
function getActionDirectories(): string[] {
  if (!fs.existsSync(actionsDir)) {
    return []
  }

  return fs
    .readdirSync(actionsDir, { withFileTypes: true })
    .filter((dir) => dir.isDirectory())
    // shared is a library, not an action
    .filter((dir) => dir.name !== 'shared')
    .filter((dir) => {
      // Only include directories that have src/index.ts
      const entryPath = path.join(actionsDir, dir.name, 'src', 'index.ts')
      return fs.existsSync(entryPath)
    })
    .map((dir) => dir.name)
}

describe('property 1: Build Output Single File', () => {
  const actionNames = getActionDirectories()

  beforeAll(() => {
    // Ensure build is run before tests
    execSync('pnpm run build', {
      cwd: path.join(import.meta.dirname),
      stdio: 'pipe',
    })
  })

  /**
   * Property: For any action in the .github/actions/ directory,
   * the dist/ directory should contain exactly one index.js file
   */
  it('should generate exactly one index.js file per action', () => {
    fc.assert(
      fc.property(fc.constantFrom(...actionNames), (actionName) => {
        const distDir = path.join(actionsDir, actionName, 'dist')

        // dist directory should exist
        expect(fs.existsSync(distDir)).toBe(true)

        // Get all files in dist directory
        const files = fs.readdirSync(distDir)

        // Should contain exactly one file
        expect(files.length).toBe(1)

        // That file should be index.js
        expect(files[0]).toBe('index.js')

        return true
      }),
      { numRuns: Math.max(actionNames.length * 10, 100) },
    )
  })

  /**
   * Property: The bundled index.js should be a valid JavaScript file
   * that contains all dependencies (no external requires that would fail)
   */
  it('should bundle all dependencies into a single file', () => {
    // Node.js built-in modules (both with and without node: prefix)
    // This includes all core modules and their subpaths
    const nodeBuiltins = new Set([
      'assert',
      'assert/strict',
      'async_hooks',
      'buffer',
      'child_process',
      'cluster',
      'console',
      'constants',
      'crypto',
      'dgram',
      'diagnostics_channel',
      'dns',
      'dns/promises',
      'domain',
      'events',
      'fs',
      'fs/promises',
      'http',
      'http2',
      'https',
      'inspector',
      'inspector/promises',
      'module',
      'net',
      'os',
      'path',
      'path/posix',
      'path/win32',
      'perf_hooks',
      'process',
      'punycode',
      'querystring',
      'readline',
      'readline/promises',
      'repl',
      'stream',
      'stream/consumers',
      'stream/promises',
      'stream/web',
      'string_decoder',
      'sys',
      'timers',
      'timers/promises',
      'tls',
      'trace_events',
      'tty',
      'url',
      'util',
      'util/types',
      'v8',
      'vm',
      'wasi',
      'worker_threads',
      'zlib',
    ])

    fc.assert(
      fc.property(fc.constantFrom(...actionNames), (actionName) => {
        const indexPath = path.join(actionsDir, actionName, 'dist', 'index.js')

        // File should exist
        expect(fs.existsSync(indexPath)).toBe(true)

        // File should have content
        const content = fs.readFileSync(indexPath, 'utf-8')
        expect(content.length).toBeGreaterThan(0)

        // File should be a valid JavaScript module (ESM format)
        // Check for ESM banner that provides __dirname compatibility
        expect(content).toContain('import { createRequire }')
        expect(content).toContain('fileURLToPath')

        // Should not have external requires to node_modules
        // (all dependencies should be bundled)
        const requireMatches = content.match(/require\(['"]([^'"]+)['"]\)/g) || []
        const unbundledDeps = requireMatches.filter((req) => {
          // Extract the module name from require('module') or require("module")
          const match = req.match(/require\(['"]([^'"]+)['"]\)/)
          if (!match) {
            return false
          }
          const moduleName = match[1]

          // Node built-ins with node: prefix are OK
          if (moduleName.startsWith('node:')) {
            return false
          }

          // Node built-ins without prefix are OK
          if (nodeBuiltins.has(moduleName)) {
            return false
          }

          // Relative imports are OK
          if (moduleName.startsWith('./') || moduleName.startsWith('../')) {
            return false
          }

          // Everything else is an unbundled dependency
          return true
        })

        // All external dependencies should be bundled
        expect(unbundledDeps.length).toBe(0)

        return true
      }),
      { numRuns: Math.max(actionNames.length * 10, 100) },
    )
  })

  /**
   * Property: Each action's action.yml should reference dist/index.js
   */
  it('should have action.yml referencing dist/index.js', () => {
    fc.assert(
      fc.property(fc.constantFrom(...actionNames), (actionName) => {
        const actionYmlPath = path.join(actionsDir, actionName, 'action.yml')

        // action.yml should exist
        expect(fs.existsSync(actionYmlPath)).toBe(true)

        // Should reference dist/index.js
        const content = fs.readFileSync(actionYmlPath, 'utf-8')
        expect(content).toContain('dist/index.js')

        return true
      }),
      { numRuns: Math.max(actionNames.length * 10, 100) },
    )
  })

  /**
   * Property: Build output should be deterministic
   * Running build twice should produce files with same content
   */
  it('should produce deterministic build output', () => {
    // Run build again
    execSync('pnpm run build', {
      cwd: path.join(import.meta.dirname),
      stdio: 'pipe',
    })

    fc.assert(
      fc.property(fc.constantFrom(...actionNames), (actionName) => {
        const indexPath = path.join(actionsDir, actionName, 'dist', 'index.js')
        const content1 = fs.readFileSync(indexPath, 'utf-8')

        // Run build one more time
        execSync('pnpm run build', {
          cwd: path.join(import.meta.dirname),
          stdio: 'pipe',
        })

        const content2 = fs.readFileSync(indexPath, 'utf-8')

        // Content should be identical
        expect(content1).toBe(content2)

        return true
      }),
      // Only run once per action for this expensive test
      { numRuns: actionNames.length },
    )
  })
})
