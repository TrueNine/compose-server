/**
 * GitHub Actions utilities for interacting with the GitHub Actions runtime
 *
 * @module github-utils
 */

import * as core from '@actions/core';
import { appendFile, writeFile } from 'node:fs/promises';

/**
 * Set an output value for the action
 *
 * @param name - Output name
 * @param value - Output value (will be converted to string)
 */
export function setOutput(name: string, value: string | number | boolean): void {
    core.setOutput(name, String(value));
}

/**
 * Get an input value from the action
 *
 * @param name - Input name
 * @param required - Whether the input is required (throws if missing)
 * @returns Input value or empty string if not found
 */
export function getInput(name: string, required = false): string {
    return core.getInput(name, { required });
}

/**
 * Get a boolean input value from the action
 *
 * @param name - Input name
 * @param required - Whether the input is required
 * @returns Boolean value
 */
export function getBooleanInput(name: string, required = false): boolean {
    return core.getBooleanInput(name, { required });
}

/**
 * Get a multiline input value from the action
 *
 * @param name - Input name
 * @param required - Whether the input is required
 * @returns Array of input lines
 */
export function getMultilineInput(name: string, required = false): string[] {
    return core.getMultilineInput(name, { required });
}

/**
 * Write content to GitHub Step Summary
 *
 * @param content - Markdown content to write
 * @param append - Whether to append to existing summary (default: true)
 */
export async function writeStepSummary(content: string, append = true): Promise<void> {
    const summaryPath = process.env.GITHUB_STEP_SUMMARY;

    if (!summaryPath) {
        // Not running in GitHub Actions, log to console instead
        console.log('Step Summary (not in GitHub Actions):');
        console.log(content);
        return;
    }

    if (append) {
        await appendFile(summaryPath, content + '\n');
    } else {
        await writeFile(summaryPath, content + '\n');
    }
}

/**
 * Log an info message
 *
 * @param message - Message to log
 */
export function info(message: string): void {
    core.info(message);
}

/**
 * Log a warning message
 *
 * @param message - Warning message
 */
export function warning(message: string): void {
    core.warning(message);
}

/**
 * Log an error message
 *
 * @param message - Error message
 */
export function error(message: string): void {
    core.error(message);
}

/**
 * Set the action as failed with an error message
 *
 * @param message - Error message
 */
export function setFailed(message: string): void {
    core.setFailed(message);
}

/**
 * Start a log group
 *
 * @param name - Group name
 */
export function startGroup(name: string): void {
    core.startGroup(name);
}

/**
 * End the current log group
 */
export function endGroup(): void {
    core.endGroup();
}

/**
 * Execute a function within a log group
 *
 * @param name - Group name
 * @param fn - Function to execute
 * @returns Result of the function
 */
export async function group<T>(name: string, fn: () => Promise<T>): Promise<T> {
    return core.group(name, fn);
}

/**
 * Export a variable to the environment for subsequent steps
 *
 * @param name - Variable name
 * @param value - Variable value
 */
export function exportVariable(name: string, value: string): void {
    core.exportVariable(name, value);
}

/**
 * Add a path to the PATH environment variable for subsequent steps
 *
 * @param inputPath - Path to add
 */
export function addPath(inputPath: string): void {
    core.addPath(inputPath);
}

/**
 * Check if the action is running in debug mode
 *
 * @returns true if debug mode is enabled
 */
export function isDebug(): boolean {
    return core.isDebug();
}

/**
 * Log a debug message (only visible when debug mode is enabled)
 *
 * @param message - Debug message
 */
export function debug(message: string): void {
    core.debug(message);
}

/**
 * Get the GitHub context information
 *
 * @returns Object with common GitHub context values
 */
export function getGitHubContext(): {
    repository: string;
    sha: string;
    ref: string;
    workflow: string;
    runId: string;
    runNumber: string;
    actor: string;
} {
    return {
        repository: process.env.GITHUB_REPOSITORY || '',
        sha: process.env.GITHUB_SHA || '',
        ref: process.env.GITHUB_REF || '',
        workflow: process.env.GITHUB_WORKFLOW || '',
        runId: process.env.GITHUB_RUN_ID || '',
        runNumber: process.env.GITHUB_RUN_NUMBER || '',
        actor: process.env.GITHUB_ACTOR || '',
    };
}

/**
 * Generate a markdown table for Step Summary
 *
 * @param headers - Table headers
 * @param rows - Table rows (array of arrays)
 * @returns Markdown table string
 */
export function generateMarkdownTable(headers: string[], rows: string[][]): string {
    const headerRow = `| ${headers.join(' | ')} |`;
    const separatorRow = `| ${headers.map(() => '---').join(' | ')} |`;
    const dataRows = rows.map((row) => `| ${row.join(' | ')} |`).join('\n');

    return `${headerRow}\n${separatorRow}\n${dataRows}`;
}

/**
 * Generate a collapsible details section for Step Summary
 *
 * @param summary - Summary text (visible when collapsed)
 * @param details - Details content (visible when expanded)
 * @returns HTML details element string
 */
export function generateCollapsible(summary: string, details: string): string {
    return `<details>\n<summary>${summary}</summary>\n\n${details}\n\n</details>`;
}
