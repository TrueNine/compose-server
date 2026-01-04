/**
 * Extract Versions Action
 *
 * Extracts version information from libs.versions.toml and sets them as action outputs.
 *
 * @module extract-versions
 */

import process from 'node:process'
import {
  extractVersions,
  generateMarkdownTable,
  getInput,
  info,
  setFailed,
  setOutput,
  TomlParseError,
  writeStepSummary,
} from '@github-actions/shared'

/**
 * Main entry point for the action
 */
async function run(): Promise<void> {
  try {
    // Get input - support both INPUT_TOML_PATH (composite action) and toml-path (node action)
    const tomlPath = getInput('toml-path') || process.env.INPUT_TOML_PATH || 'gradle/libs.versions.toml'

    info(`Extracting versions from: ${tomlPath}`)

    // Extract versions from TOML file
    const versions = await extractVersions(tomlPath)

    // Set outputs
    setOutput('java-version', versions.java)
    setOutput('gradle-version', versions.gradle)
    setOutput('project-version', versions.project)

    // Log extracted versions
    info(`Java version: ${versions.java}`)
    info(`Gradle version: ${versions.gradle}`)
    info(`Project version: ${versions.project}`)

    // Write step summary
    const summaryTable = generateMarkdownTable(
      ['Version Type', 'Value'],
      [
        ['Java', versions.java],
        ['Gradle', versions.gradle],
        ['Project', versions.project],
      ],
    )

    await writeStepSummary(`## 📦 Extracted Versions\n\n${summaryTable}`)
  } catch (error) {
    if (error instanceof TomlParseError) {
      setFailed(`Failed to parse TOML: ${error.message}`)
    } else if (error instanceof Error) {
      setFailed(`Action failed: ${error.message}`)
    } else {
      setFailed('Action failed with unknown error')
    }
  }
}

// Run the action
run()
