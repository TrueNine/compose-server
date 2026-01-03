/**
 * Cache Keys Action
 *
 * Generates consistent cache keys for Gradle builds based on gradle files hash.
 * The cache keys are prefixed with the runner OS for cross-platform compatibility.
 *
 * @module cache-keys
 */

import {
    generateCacheKeys,
    setOutput,
    info,
    setFailed,
    writeStepSummary,
    generateMarkdownTable,
    startGroup,
    endGroup,
} from '@github-actions/shared';

/**
 * Main entry point for the action
 */
async function run(): Promise<void> {
    try {
        info('Generating cache keys for Gradle...');

        startGroup('Cache Key Generation');

        // Generate cache keys from workspace root
        const cacheKeys = await generateCacheKeys('.');

        info(`Gradle cache key: ${cacheKeys.gradleCache}`);
        info(`Dependencies cache key: ${cacheKeys.depsCache}`);

        endGroup();

        // Set outputs
        setOutput('gradle-cache-key', cacheKeys.gradleCache);
        setOutput('deps-cache-key', cacheKeys.depsCache);

        // Write step summary
        const summaryTable = generateMarkdownTable(
            ['Cache Type', 'Key'],
            [
                ['Gradle Cache', `\`${cacheKeys.gradleCache}\``],
                ['Dependencies Cache', `\`${cacheKeys.depsCache}\``],
            ]
        );

        await writeStepSummary(`## 🔑 Generated Cache Keys\n\n${summaryTable}\n\n### Files Included\n\n- \`gradle/wrapper/gradle-wrapper.properties\`\n- \`gradle/libs.versions.toml\`\n- \`build-logic/**/*.gradle.kts\`\n- \`build-logic/**/*.kt\`\n- \`build.gradle.kts\`\n- \`settings.gradle.kts\`\n- \`gradle.properties\``);

        info('Cache keys generated successfully!');
    } catch (error) {
        if (error instanceof Error) {
            setFailed(`Action failed: ${error.message}`);
        } else {
            setFailed('Action failed with unknown error');
        }
    }
}

// Run the action
run();
