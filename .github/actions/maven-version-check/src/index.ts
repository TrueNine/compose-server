/**
 * Maven Version Check Action
 *
 * Checks if a version already exists on Maven Central and determines
 * whether publishing should proceed. Supports force publish flag and
 * implements retry mechanism with exponential backoff.
 *
 * @module maven-version-check
 */

import type { MavenCheckResult } from '@github-actions/shared';
import {
    MavenCentralError,
    setOutput,
    getInput,
    getBooleanInput,
    info,
    warning,
    setFailed,
    writeStepSummary,
    generateMarkdownTable,
    startGroup,
    endGroup,
    isValidSemver,
    isPrerelease,
    parseVersion,
} from '@github-actions/shared';

/** Default retry configuration */
const DEFAULT_RETRY_COUNT = 3;
const DEFAULT_RETRY_DELAYS = [1000, 2000, 4000]; // Exponential backoff: 1s, 2s, 4s

/** Maven Central repository URL */
const MAVEN_CENTRAL_BASE_URL = 'https://repo1.maven.org/maven2';

/**
 * Sleep for specified milliseconds
 */
function sleep(ms: number): Promise<void> {
    return new Promise((resolve) => setTimeout(resolve, ms));
}

/**
 * Build Maven Central artifact URL
 *
 * @param groupId - Maven group ID (e.g., "io.github.truenine")
 * @param artifactId - Maven artifact ID (e.g., "composeserver-shared")
 * @param version - Version to check
 * @returns Full URL to the artifact POM file
 */
export function buildMavenCentralUrl(groupId: string, artifactId: string, version: string): string {
    const groupPath = groupId.replace(/\./g, '/');
    return `${MAVEN_CENTRAL_BASE_URL}/${groupPath}/${artifactId}/${version}/${artifactId}-${version}.pom`;
}

/**
 * Check if an artifact exists on Maven Central with retry mechanism
 *
 * @param groupId - Maven group ID
 * @param artifactId - Maven artifact ID
 * @param version - Version to check
 * @param retryCount - Number of retries on failure
 * @returns true if artifact exists, false otherwise
 * @throws {MavenCentralError} When all retries are exhausted
 */
export async function checkArtifactExists(
    groupId: string,
    artifactId: string,
    version: string,
    retryCount: number = DEFAULT_RETRY_COUNT
): Promise<boolean> {
    const url = buildMavenCentralUrl(groupId, artifactId, version);

    for (let attempt = 0; attempt < retryCount; attempt++) {
        try {
            const response = await fetch(url, {
                method: 'HEAD',
                headers: {
                    'User-Agent': 'GitHub-Actions-Maven-Version-Check/1.0',
                },
            });

            if (response.status === 200) {
                return true;
            }

            if (response.status === 404) {
                return false;
            }

            // For other status codes, we might want to retry
            if (response.status >= 500) {
                throw new Error(`Server error: ${response.status}`);
            }

            // Client errors (4xx except 404) - don't retry
            return false;
        } catch (error) {
            const isLastAttempt = attempt === retryCount - 1;

            if (isLastAttempt) {
                throw new MavenCentralError(
                    error instanceof Error ? error.message : 'Unknown error',
                    artifactId,
                    undefined,
                    attempt + 1
                );
            }

            // Wait before retry with exponential backoff
            const delay = DEFAULT_RETRY_DELAYS[attempt] ?? 4000;
            warning(`Attempt ${attempt + 1} failed for ${artifactId}, retrying in ${delay}ms...`);
            await sleep(delay);
        }
    }

    return false;
}

/**
 * Check multiple artifacts on Maven Central
 *
 * @param groupId - Maven group ID
 * @param artifacts - List of artifact IDs to check
 * @param version - Version to check
 * @returns Object with artifact existence status
 */
export async function checkMultipleArtifacts(
    groupId: string,
    artifacts: string[],
    version: string
): Promise<Map<string, boolean>> {
    const results = new Map<string, boolean>();

    for (const artifact of artifacts) {
        try {
            const exists = await checkArtifactExists(groupId, artifact, version);
            results.set(artifact, exists);
            info(`  ${artifact}: ${exists ? '✓ exists' : '✗ not found'}`);
        } catch (error) {
            warning(`  ${artifact}: ⚠ check failed - ${error instanceof Error ? error.message : 'unknown error'}`);
            results.set(artifact, false);
        }
    }

    return results;
}

/**
 * Perform Maven Central version check
 *
 * @param version - Version to check
 * @param groupId - Maven group ID
 * @param artifacts - List of artifact IDs to check
 * @param forcePublish - Skip existence check
 * @returns MavenCheckResult with check results
 */
export async function performVersionCheck(
    version: string,
    groupId: string,
    artifacts: string[],
    forcePublish: boolean
): Promise<MavenCheckResult> {
    // Parse and validate version
    const cleanVersion = parseVersion(version);
    const isSnapshot = isPrerelease(cleanVersion);
    const isValid = isValidSemver(cleanVersion);

    if (!isValid) {
        warning(`Version "${cleanVersion}" does not follow strict semver format`);
    }

    // If force publish, skip existence check
    if (forcePublish) {
        info('Force publish enabled, skipping existence check');
        return {
            version: cleanVersion,
            isSnapshot,
            shouldPublish: true,
            versionExistsOnCentral: false,
        };
    }

    // Snapshot versions are never on Maven Central
    if (isSnapshot) {
        info('Snapshot/prerelease version detected, skipping Maven Central check');
        return {
            version: cleanVersion,
            isSnapshot,
            shouldPublish: true,
            versionExistsOnCentral: false,
        };
    }

    // Check artifacts on Maven Central
    info(`Checking ${artifacts.length} artifact(s) on Maven Central...`);
    const artifactResults = await checkMultipleArtifacts(groupId, artifacts, cleanVersion);

    // If ANY artifact exists, consider version as existing
    const anyExists = Array.from(artifactResults.values()).some((exists) => exists);

    return {
        version: cleanVersion,
        isSnapshot,
        shouldPublish: !anyExists,
        versionExistsOnCentral: anyExists,
    };
}

/**
 * Generate step summary markdown
 */
function generateSummary(result: MavenCheckResult, groupId: string, artifacts: string[]): string {
    const statusIcon = result.shouldPublish ? '✅' : '⏭️';
    const statusText = result.shouldPublish ? 'Will Publish' : 'Skip (Already Exists)';

    let summary = `## ${statusIcon} Maven Central Version Check\n\n`;

    summary += generateMarkdownTable(
        ['Property', 'Value'],
        [
            ['Version', `\`${result.version}\``],
            ['Group ID', `\`${groupId}\``],
            ['Is Prerelease', result.isSnapshot ? 'Yes' : 'No'],
            ['Exists on Maven Central', result.versionExistsOnCentral ? 'Yes' : 'No'],
            ['Status', statusText],
        ]
    );

    summary += '\n\n### Artifacts Checked\n\n';
    summary += artifacts.map((a) => `- \`${groupId}:${a}\``).join('\n');

    if (result.versionExistsOnCentral) {
        summary += '\n\n> ℹ️ Version already exists on Maven Central. Publishing will be skipped.';
    }

    return summary;
}

/**
 * Main entry point for the action
 */
async function run(): Promise<void> {
    try {
        // Get inputs
        const version = getInput('version', true);
        const groupId = getInput('group-id') || 'io.github.truenine';
        const artifactsInput = getInput('artifacts') || 'composeserver-shared,composeserver-cacheable,composeserver-bom';
        const forcePublish = getBooleanInput('force-publish');

        // Parse artifacts list
        const artifacts = artifactsInput
            .split(',')
            .map((a) => a.trim())
            .filter((a) => a.length > 0);

        info(`Checking version: ${version}`);
        info(`Group ID: ${groupId}`);
        info(`Artifacts: ${artifacts.join(', ')}`);
        info(`Force publish: ${forcePublish}`);

        startGroup('Maven Central Version Check');

        // Perform version check
        const result = await performVersionCheck(version, groupId, artifacts, forcePublish);

        endGroup();

        // Set outputs
        setOutput('should-publish', String(result.shouldPublish));
        setOutput('version-exists', String(result.versionExistsOnCentral));
        setOutput('is-snapshot', String(result.isSnapshot));

        // Write step summary
        const summary = generateSummary(result, groupId, artifacts);
        await writeStepSummary(summary);

        // Log final result
        if (result.shouldPublish) {
            info(`✅ Version ${result.version} should be published`);
        } else {
            info(`⏭️ Version ${result.version} already exists, skipping publish`);
        }
    } catch (error) {
        if (error instanceof MavenCentralError) {
            setFailed(`Maven Central check failed for ${error.artifact}: ${error.message}`);
        } else if (error instanceof Error) {
            setFailed(`Action failed: ${error.message}`);
        } else {
            setFailed('Action failed with unknown error');
        }
    }
}

// Run the action
run();
