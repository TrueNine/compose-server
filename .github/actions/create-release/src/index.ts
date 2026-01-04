/**
 * Create Release Action
 *
 * Creates a GitHub Release with generated release notes.
 * Supports idempotent operation - skips creation if release already exists.
 *
 * @module create-release
 */

import process from 'node:process'
import * as github from '@actions/github'
import {
  generateMarkdownTable,
  getBooleanInput,
  getInput,
  info,
  isPrerelease,
  parseVersion,
  setFailed,
  setOutput,
  warning,
  writeStepSummary,
} from '@github-actions/shared'

/**
 * Release creation options
 */
export interface ReleaseOptions {
  /** Version to release */
  version: string
  /** Maven group ID */
  groupId: string
  /** List of artifacts */
  artifacts: string[]
  /** Create as draft release */
  draft: boolean
  /** Auto-generate release notes */
  generateNotes: boolean
  /** GitHub token */
  token: string
}

/**
 * Release creation result
 */
export interface ReleaseResult {
  /** Release ID */
  releaseId: number
  /** Release URL */
  releaseUrl: string
  /** Whether a new release was created */
  releaseCreated: boolean
  /** Tag name */
  tagName: string
}

/**
 * Generate tag name from version
 *
 * @param version - Version string
 * @returns Tag name with 'v' prefix
 */
export function generateTagName(version: string): string {
  const cleanVersion = parseVersion(version)
  return `v${cleanVersion}`
}

/**
 * Generate Maven Central URL for an artifact
 *
 * @param groupId - Maven group ID
 * @param artifactId - Maven artifact ID
 * @returns Maven Central search URL
 */
export function getMavenSearchUrl(groupId: string, artifactId: string): string {
  return `https://central.sonatype.com/artifact/${groupId}/${artifactId}`
}

/**
 * Generate release notes template
 *
 * @param version - Version being released
 * @param groupId - Maven group ID
 * @param artifacts - List of artifacts
 * @returns Release notes markdown
 */
export function generateReleaseNotes(
  version: string,
  groupId: string,
  artifacts: string[],
): string {
  const cleanVersion = parseVersion(version)
  const isPre = isPrerelease(cleanVersion)

  let notes = ''

  // Header
  if (isPre) {
    notes += `## ⚠️ Pre-release ${cleanVersion}\n\n`
    notes += `> **Note**: This is a pre-release version and may contain unstable features.\n\n`
  } else {
    notes += `## Release ${cleanVersion}\n\n`
  }

  // Maven coordinates section
  notes += `### 📦 Maven Coordinates\n\n`
  notes += `**Group ID**: \`${groupId}\`\n`
  notes += `**Version**: \`${cleanVersion}\`\n\n`

  // Artifacts table
  if (artifacts.length > 0) {
    notes += `### Available Artifacts\n\n`
    notes += `| Artifact | Maven Central |\n`
    notes += `| --- | --- |\n`
    for (const artifact of artifacts) {
      const url = getMavenSearchUrl(groupId, artifact)
      notes += `| \`${artifact}\` | [View](${url}) |\n`
    }
    notes += '\n'
  }

  // Usage examples
  notes += `### 📝 Usage\n\n`
  notes += `<details>\n<summary>Gradle (Kotlin DSL)</summary>\n\n`
  notes += '```kotlin\n'
  notes += `dependencies {\n`
  if (artifacts.length > 0) {
    notes += `    implementation("${groupId}:${artifacts[0]}:${cleanVersion}")\n`
  } else {
    notes += `    implementation("${groupId}:your-artifact:${cleanVersion}")\n`
  }
  notes += `}\n`
  notes += '```\n\n'
  notes += `</details>\n\n`

  notes += `<details>\n<summary>Maven</summary>\n\n`
  notes += '```xml\n'
  notes += `<dependency>\n`
  notes += `    <groupId>${groupId}</groupId>\n`
  if (artifacts.length > 0) {
    notes += `    <artifactId>${artifacts[0]}</artifactId>\n`
  } else {
    notes += `    <artifactId>your-artifact</artifactId>\n`
  }
  notes += `    <version>${cleanVersion}</version>\n`
  notes += `</dependency>\n`
  notes += '```\n\n'
  notes += `</details>\n\n`

  return notes
}

/**
 * Check if a release already exists for the given tag
 *
 * @param octokit - GitHub API client
 * @param owner - Repository owner
 * @param repo - Repository name
 * @param tagName - Tag name to check
 * @returns Existing release or null
 */
async function findExistingRelease(
  octokit: ReturnType<typeof github.getOctokit>,
  owner: string,
  repo: string,
  tagName: string,
): Promise<{ id: number, html_url: string } | null> {
  try {
    const { data: release } = await octokit.rest.repos.getReleaseByTag({
      owner,
      repo,
      tag: tagName,
    })
    return { id: release.id, html_url: release.html_url }
  } catch (err) {
    // 404 means release doesn't exist
    if (err instanceof Error && 'status' in err && (err as { status: number }).status === 404) {
      return null
    }
    throw err
  }
}

/**
 * Create a new GitHub release
 *
 * @param options - Release options
 * @returns Release result
 */
export async function createRelease(options: ReleaseOptions): Promise<ReleaseResult> {
  const { version, groupId, artifacts, draft, generateNotes, token } = options

  const cleanVersion = parseVersion(version)
  const tagName = generateTagName(cleanVersion)
  const isPre = isPrerelease(cleanVersion)

  // Get repository context
  const context = github.context
  const owner = context.repo.owner
  const repo = context.repo.repo

  info(`Creating release for ${tagName} in ${owner}/${repo}`)
  info(`Is prerelease: ${isPre}`)
  info(`Draft: ${draft}`)

  // Create GitHub API client
  const octokit = github.getOctokit(token)

  // Check if release already exists (idempotency)
  const existingRelease = await findExistingRelease(octokit, owner, repo, tagName)
  if (existingRelease) {
    warning(`Release ${tagName} already exists, skipping creation`)
    return {
      releaseId: existingRelease.id,
      releaseUrl: existingRelease.html_url,
      releaseCreated: false,
      tagName,
    }
  }

  // Generate release notes
  const releaseNotes = generateReleaseNotes(cleanVersion, groupId, artifacts)

  // Create the release
  const { data: release } = await octokit.rest.repos.createRelease({
    owner,
    repo,
    tag_name: tagName,
    name: `Release ${cleanVersion}`,
    body: releaseNotes,
    draft,
    prerelease: isPre,
    generate_release_notes: generateNotes,
  })

  info(`✅ Created release: ${release.html_url}`)

  return {
    releaseId: release.id,
    releaseUrl: release.html_url,
    releaseCreated: true,
    tagName,
  }
}

/**
 * Generate step summary for the release
 *
 * @param result - Release result
 * @param options - Release options
 */
async function generateSummary(result: ReleaseResult, options: ReleaseOptions): Promise<void> {
  const { version, groupId, artifacts } = options
  const cleanVersion = parseVersion(version)
  const isPre = isPrerelease(cleanVersion)

  let summary = ''

  if (result.releaseCreated) {
    summary += `## ✅ GitHub Release Created\n\n`
  } else {
    summary += `## ℹ️ GitHub Release Already Exists\n\n`
  }

  summary += generateMarkdownTable(
    ['Property', 'Value'],
    [
      ['Version', `\`${cleanVersion}\``],
      ['Tag', `\`${result.tagName}\``],
      ['Is Prerelease', isPre ? 'Yes' : 'No'],
      ['Status', result.releaseCreated ? 'Created' : 'Already Exists'],
      ['Release URL', `[View Release](${result.releaseUrl})`],
    ],
  )
  summary += '\n\n'

  if (artifacts.length > 0) {
    summary += `### Published Artifacts\n\n`
    for (const artifact of artifacts) {
      const url = getMavenSearchUrl(groupId, artifact)
      summary += `- [\`${artifact}\`](${url})\n`
    }
    summary += '\n'
  }

  await writeStepSummary(summary)
}

/**
 * Main entry point for the action
 */
async function run(): Promise<void> {
  try {
    // Parse inputs
    const version = getInput('version', true)
    const groupId = getInput('group-id') || 'io.github.truenine'
    const artifactsInput = getInput('artifacts') || ''
    const artifacts = artifactsInput
      .split(',')
      .map((a) => a.trim())
      .filter((a) => a.length > 0)
    const draft = getBooleanInput('draft')
    const generateNotes = getBooleanInput('generate-notes')
    const token = process.env.GITHUB_TOKEN || ''

    if (!token) {
      throw new Error('GITHUB_TOKEN is required')
    }

    info(`Version: ${version}`)
    info(`Group ID: ${groupId}`)
    info(`Artifacts: ${artifacts.length > 0 ? artifacts.join(', ') : 'none'}`)
    info(`Draft: ${draft}`)
    info(`Generate Notes: ${generateNotes}`)

    const options: ReleaseOptions = {
      version,
      groupId,
      artifacts,
      draft,
      generateNotes,
      token,
    }

    // Create release
    const result = await createRelease(options)

    // Set outputs
    setOutput('release-id', result.releaseId)
    setOutput('release-url', result.releaseUrl)
    setOutput('release-created', result.releaseCreated)
    setOutput('tag-name', result.tagName)

    // Generate summary
    await generateSummary(result, options)

    if (result.releaseCreated) {
      info(`✅ Successfully created release ${result.tagName}`)
    } else {
      info(`ℹ️ Release ${result.tagName} already exists`)
    }
  } catch (err) {
    if (err instanceof Error) {
      setFailed(`Action failed: ${err.message}`)
    } else {
      setFailed('Action failed with unknown error')
    }
  }
}

// Run the action
run()
