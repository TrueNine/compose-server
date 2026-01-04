import * as fs from 'node:fs'
import * as path from 'node:path'
import process from 'node:process'
import * as esbuild from 'esbuild'

const actionsDir = path.join(import.meta.dirname, 'actions')

/**
 * Get all action directories that have a src/index.ts file
 */
function getActionEntries(): Array<{ name: string, entry: string, outdir: string }> {
  const entries: Array<{ name: string, entry: string, outdir: string }> = []

  if (!fs.existsSync(actionsDir)) {
    console.warn('Actions directory does not exist:', actionsDir)
    return entries
  }

  const dirs = fs.readdirSync(actionsDir, { withFileTypes: true })

  for (const dir of dirs) {
    if (!dir.isDirectory()) {
      continue
    }
    if (dir.name === 'shared') {
      // shared is a library, not an action
      continue
    }

    const entryPath = path.join(actionsDir, dir.name, 'src', 'index.ts')
    if (fs.existsSync(entryPath)) {
      entries.push({
        name: dir.name,
        entry: entryPath,
        outdir: path.join(actionsDir, dir.name, 'dist'),
      })
    }
  }

  return entries
}

async function build(): Promise<void> {
  const entries = getActionEntries()

  if (entries.length === 0) {
    console.log('No actions found to build.')
    return
  }

  console.log(`Building ${entries.length} action(s)...`)

  const buildPromises = entries.map(async ({ name, entry, outdir }) => {
    console.log(`  Building ${name}...`)

    await esbuild.build({
      entryPoints: [entry],
      bundle: true,
      platform: 'node',
      target: 'node22',
      format: 'esm',
      outfile: path.join(outdir, 'index.js'),
      sourcemap: false,
      minify: true,
      treeShaking: true,
      // External packages that should not be bundled
      external: [],
      // Banner to make ESM work with __dirname
      banner: {
        js: `
import { createRequire } from 'module';
import { fileURLToPath } from 'url';
import { dirname } from 'path';
const require = createRequire(import.meta.url);
const __filename = fileURLToPath(import.meta.url);
const __dirname = dirname(__filename);
`.trim(),
      },
    })

    console.log(`  ✓ ${name} built successfully`)
  })

  await Promise.all(buildPromises)
  console.log('All actions built successfully!')
}

build().catch((error: unknown) => {
  console.error('Build failed:', error)
  process.exit(1)
})
