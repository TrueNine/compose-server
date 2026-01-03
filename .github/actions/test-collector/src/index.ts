/**
 * Test Result Collector Action
 *
 * Collects and summarizes test results from Gradle test executions.
 * Generates GitHub Step Summary with test status and provides
 * troubleshooting suggestions for failures.
 *
 * @module test-collector
 */

import { readFile } from 'node:fs/promises';
import { join } from 'node:path';
import type { TestResult } from '@github-actions/shared';
import {
    setOutput,
    getInput,
    getBooleanInput,
    info,
    warning,
    error as logError,
    setFailed,
    writeStepSummary,
    generateMarkdownTable,
    generateCollapsible,
    startGroup,
    endGroup,
    findFiles,
} from '@github-actions/shared';

/**
 * JUnit XML test case result
 */
interface JUnitTestCase {
    name: string;
    classname: string;
    time: number;
    failure?: {
        message: string;
        type: string;
        content: string;
    };
    error?: {
        message: string;
        type: string;
        content: string;
    };
    skipped?: boolean;
}

/**
 * JUnit XML test suite result
 */
interface JUnitTestSuite {
    name: string;
    tests: number;
    failures: number;
    errors: number;
    skipped: number;
    time: number;
    testCases: JUnitTestCase[];
}

/**
 * Aggregated test results from all modules
 */
interface AggregatedResults {
    totalTests: number;
    failedTests: number;
    errorTests: number;
    skippedTests: number;
    duration: number;
    suites: JUnitTestSuite[];
    failedCases: JUnitTestCase[];
}

/**
 * Parse a JUnit XML test result file
 *
 * @param content - XML content
 * @returns Parsed test suite
 */
export function parseJUnitXml(content: string): JUnitTestSuite {
    // Simple XML parsing for JUnit format
    // Extract testsuite attributes
    const suiteMatch = content.match(/<testsuite[^>]*>/);
    if (!suiteMatch) {
        return {
            name: 'Unknown',
            tests: 0,
            failures: 0,
            errors: 0,
            skipped: 0,
            time: 0,
            testCases: [],
        };
    }

    const suiteTag = suiteMatch[0];
    const name = extractAttribute(suiteTag, 'name') || 'Unknown';
    const tests = parseInt(extractAttribute(suiteTag, 'tests') || '0', 10);
    const failures = parseInt(extractAttribute(suiteTag, 'failures') || '0', 10);
    const errors = parseInt(extractAttribute(suiteTag, 'errors') || '0', 10);
    const skipped = parseInt(extractAttribute(suiteTag, 'skipped') || '0', 10);
    const time = parseFloat(extractAttribute(suiteTag, 'time') || '0');

    // Parse test cases - handle both self-closing and regular tags
    const testCases: JUnitTestCase[] = [];

    // First, find all self-closing testcase tags: <testcase ... />
    const selfClosingRegex = /<testcase[^>]*\/>/g;
    let selfClosingMatch;
    while ((selfClosingMatch = selfClosingRegex.exec(content)) !== null) {
        const testCaseXml = selfClosingMatch[0];
        const testCase = parseTestCase(testCaseXml);
        testCases.push(testCase);
    }

    // Then, find all regular testcase tags: <testcase ...>...</testcase>
    // Use a non-greedy match that stops at the first </testcase>
    const regularRegex = /<testcase(?![^>]*\/>)[^>]*>[\s\S]*?<\/testcase>/g;
    let regularMatch;
    while ((regularMatch = regularRegex.exec(content)) !== null) {
        const testCaseXml = regularMatch[0];
        const testCase = parseTestCase(testCaseXml);
        testCases.push(testCase);
    }

    return {
        name,
        tests,
        failures,
        errors,
        skipped,
        time,
        testCases,
    };
}

/**
 * Extract an attribute value from an XML tag
 *
 * @param tag - XML tag string
 * @param attr - Attribute name
 * @returns Attribute value or undefined
 */
function extractAttribute(tag: string, attr: string): string | undefined {
    const regex = new RegExp(`${attr}="([^"]*)"`, 'i');
    const match = tag.match(regex);
    return match ? match[1] : undefined;
}

/**
 * Parse a single test case from XML
 *
 * @param xml - Test case XML string
 * @returns Parsed test case
 */
function parseTestCase(xml: string): JUnitTestCase {
    const tagMatch = xml.match(/<testcase[^>]*>/);
    const tag = tagMatch ? tagMatch[0] : '';

    const testCase: JUnitTestCase = {
        name: extractAttribute(tag, 'name') || 'Unknown',
        classname: extractAttribute(tag, 'classname') || 'Unknown',
        time: parseFloat(extractAttribute(tag, 'time') || '0'),
    };

    // Check for failure
    const failureMatch = xml.match(/<failure[^>]*>([\s\S]*?)<\/failure>/);
    if (failureMatch && failureMatch[1]) {
        const failureTag = xml.match(/<failure[^>]*>/)?.[0] || '';
        testCase.failure = {
            message: extractAttribute(failureTag, 'message') || '',
            type: extractAttribute(failureTag, 'type') || '',
            content: failureMatch[1].trim(),
        };
    }

    // Check for error
    const errorMatch = xml.match(/<error[^>]*>([\s\S]*?)<\/error>/);
    if (errorMatch && errorMatch[1]) {
        const errorTag = xml.match(/<error[^>]*>/)?.[0] || '';
        testCase.error = {
            message: extractAttribute(errorTag, 'message') || '',
            type: extractAttribute(errorTag, 'type') || '',
            content: errorMatch[1].trim(),
        };
    }

    // Check for skipped
    if (xml.includes('<skipped')) {
        testCase.skipped = true;
    }

    return testCase;
}

/**
 * Find all JUnit XML test result files
 *
 * @param basePath - Base path to search from
 * @param modules - List of modules to search in
 * @returns Array of file paths
 */
export async function findTestResultFiles(basePath: string, modules: string[]): Promise<string[]> {
    const patterns: string[] = [];

    for (const module of modules) {
        // Handle module paths like "rds:rds-shared" -> "rds/rds-shared"
        const modulePath = module.replace(/:/g, '/');
        patterns.push(join(basePath, modulePath, '**/build/test-results/**/*.xml'));
    }

    // Also search in root build directories
    patterns.push(join(basePath, '**/build/test-results/**/*.xml'));

    const files = await findFiles(patterns);

    // Filter out duplicates and non-test files
    const uniqueFiles = [...new Set(files)].filter(
        (f) => f.endsWith('.xml') && !f.includes('binary')
    );

    return uniqueFiles;
}

/**
 * Aggregate test results from multiple files
 *
 * @param files - Array of test result file paths
 * @returns Aggregated results
 */
export async function aggregateTestResults(files: string[]): Promise<AggregatedResults> {
    const results: AggregatedResults = {
        totalTests: 0,
        failedTests: 0,
        errorTests: 0,
        skippedTests: 0,
        duration: 0,
        suites: [],
        failedCases: [],
    };

    for (const file of files) {
        try {
            const content = await readFile(file, 'utf-8');
            const suite = parseJUnitXml(content);

            results.totalTests += suite.tests;
            results.failedTests += suite.failures;
            results.errorTests += suite.errors;
            results.skippedTests += suite.skipped;
            results.duration += suite.time;
            results.suites.push(suite);

            // Collect failed test cases
            for (const testCase of suite.testCases) {
                if (testCase.failure || testCase.error) {
                    results.failedCases.push(testCase);
                }
            }
        } catch (err) {
            warning(`Failed to parse test result file ${file}: ${err instanceof Error ? err.message : 'Unknown error'}`);
        }
    }

    return results;
}

/**
 * Generate TestContainers diagnostics section
 *
 * @returns Markdown string with diagnostics
 */
function generateTestContainersDiagnostics(): string {
    let diagnostics = '### 🐳 TestContainers Diagnostics\n\n';

    diagnostics += 'TestContainers was used in this test run. Common issues:\n\n';
    diagnostics += '- **Docker not running**: Ensure Docker daemon is running\n';
    diagnostics += '- **Resource limits**: Check Docker memory/CPU limits\n';
    diagnostics += '- **Network issues**: Verify container network connectivity\n';
    diagnostics += '- **Image pull failures**: Check network access to Docker Hub\n';
    diagnostics += '- **Port conflicts**: Ensure required ports are available\n\n';

    diagnostics += '**Useful commands:**\n';
    diagnostics += '```bash\n';
    diagnostics += '# Check Docker status\n';
    diagnostics += 'docker info\n\n';
    diagnostics += '# List running containers\n';
    diagnostics += 'docker ps\n\n';
    diagnostics += '# Check container logs\n';
    diagnostics += 'docker logs <container_id>\n';
    diagnostics += '```\n';

    return diagnostics;
}

/**
 * Generate troubleshooting suggestions based on failures
 *
 * @param results - Aggregated test results
 * @param useTestContainers - Whether TestContainers was used
 * @returns Markdown string with suggestions
 */
function generateTroubleshootingSuggestions(
    results: AggregatedResults,
    useTestContainers: boolean
): string {
    let suggestions = '### 🔍 Troubleshooting Suggestions\n\n';

    if (results.failedCases.length === 0) {
        return '';
    }

    // Analyze failure patterns
    const failureTypes = new Map<string, number>();
    for (const testCase of results.failedCases) {
        const type = testCase.failure?.type || testCase.error?.type || 'Unknown';
        failureTypes.set(type, (failureTypes.get(type) || 0) + 1);
    }

    suggestions += '**Failure Analysis:**\n\n';
    for (const [type, count] of failureTypes) {
        suggestions += `- \`${type}\`: ${count} occurrence(s)\n`;
    }
    suggestions += '\n';

    // Common suggestions
    suggestions += '**General Suggestions:**\n\n';
    suggestions += '1. Run tests locally to reproduce the issue:\n';
    suggestions += '   ```bash\n';
    suggestions += '   ./gradlew test --tests "ClassName.methodName"\n';
    suggestions += '   ```\n\n';

    suggestions += '2. Check for flaky tests by running multiple times:\n';
    suggestions += '   ```bash\n';
    suggestions += '   ./gradlew test --rerun-tasks\n';
    suggestions += '   ```\n\n';

    suggestions += '3. Review test logs in the artifacts\n\n';

    // TestContainers specific suggestions
    if (useTestContainers) {
        suggestions += generateTestContainersDiagnostics();
    }

    return suggestions;
}

/**
 * Generate the test summary markdown
 *
 * @param groupName - Test group name
 * @param modules - List of modules tested
 * @param results - Aggregated test results
 * @param useTestContainers - Whether TestContainers was used
 * @returns Markdown summary string
 */
export function generateTestSummary(
    groupName: string,
    modules: string[],
    results: AggregatedResults,
    useTestContainers: boolean
): string {
    const passed = results.failedTests === 0 && results.errorTests === 0;
    const statusIcon = passed ? '✅' : '❌';
    const statusText = passed ? 'Passed' : 'Failed';

    let summary = `## ${statusIcon} Test Results: ${groupName}\n\n`;

    // Overview table
    summary += generateMarkdownTable(
        ['Metric', 'Value'],
        [
            ['Status', `**${statusText}**`],
            ['Total Tests', `${results.totalTests}`],
            ['Passed', `${results.totalTests - results.failedTests - results.errorTests - results.skippedTests}`],
            ['Failed', `${results.failedTests}`],
            ['Errors', `${results.errorTests}`],
            ['Skipped', `${results.skippedTests}`],
            ['Duration', `${results.duration.toFixed(2)}s`],
            ['TestContainers', useTestContainers ? 'Yes' : 'No'],
        ]
    );

    summary += '\n\n';

    // Modules tested
    summary += '### 📦 Modules Tested\n\n';
    for (const module of modules) {
        summary += `- \`${module}\`\n`;
    }
    summary += '\n';

    // Failed tests details
    if (results.failedCases.length > 0) {
        summary += '### ❌ Failed Tests\n\n';

        for (const testCase of results.failedCases.slice(0, 10)) {
            const errorInfo = testCase.failure || testCase.error;
            const errorType = errorInfo?.type || 'Unknown';
            const errorMessage = errorInfo?.message || 'No message';

            summary += `#### \`${testCase.classname}.${testCase.name}\`\n\n`;
            summary += `- **Type**: \`${errorType}\`\n`;
            summary += `- **Message**: ${errorMessage}\n`;

            if (errorInfo?.content) {
                const truncatedContent = errorInfo.content.length > 500
                    ? errorInfo.content.substring(0, 500) + '...'
                    : errorInfo.content;
                summary += '\n' + generateCollapsible(
                    'Stack Trace',
                    '```\n' + truncatedContent + '\n```'
                );
            }
            summary += '\n';
        }

        if (results.failedCases.length > 10) {
            summary += `\n*... and ${results.failedCases.length - 10} more failed tests*\n\n`;
        }

        // Add troubleshooting suggestions
        summary += generateTroubleshootingSuggestions(results, useTestContainers);
    }

    // Test suites summary (collapsible)
    if (results.suites.length > 0) {
        const suiteRows = results.suites.map((suite) => [
            suite.name.length > 50 ? suite.name.substring(0, 47) + '...' : suite.name,
            `${suite.tests}`,
            `${suite.failures + suite.errors}`,
            `${suite.time.toFixed(2)}s`,
        ]);

        const suiteTable = generateMarkdownTable(
            ['Suite', 'Tests', 'Failed', 'Duration'],
            suiteRows.slice(0, 20)
        );

        summary += '\n' + generateCollapsible(
            `📋 Test Suites (${results.suites.length} total)`,
            suiteTable + (results.suites.length > 20 ? '\n\n*... and more suites*' : '')
        );
    }

    return summary;
}

/**
 * Convert aggregated results to TestResult interface
 *
 * @param groupName - Test group name
 * @param results - Aggregated results
 * @returns TestResult object
 */
function toTestResult(groupName: string, results: AggregatedResults): TestResult {
    return {
        group: groupName,
        passed: results.failedTests === 0 && results.errorTests === 0,
        totalTests: results.totalTests,
        failedTests: results.failedTests + results.errorTests,
        skippedTests: results.skippedTests,
        duration: results.duration,
    };
}

/**
 * Main entry point for the action
 */
async function run(): Promise<void> {
    try {
        // Parse inputs
        const groupName = getInput('group-name', true);
        const modulesInput = getInput('modules', true);
        const modules = modulesInput.split(/\s+/).filter((m) => m.length > 0);
        const useTestContainers = getBooleanInput('testcontainers');
        const uploadArtifacts = getBooleanInput('upload-artifacts');
        const basePath = getInput('base-path') || '.';

        info(`Test Group: ${groupName}`);
        info(`Modules: ${modules.join(', ')}`);
        info(`TestContainers: ${useTestContainers}`);
        info(`Upload Artifacts: ${uploadArtifacts}`);
        info(`Base Path: ${basePath}`);

        startGroup('Finding Test Result Files');

        // Find test result files
        const testFiles = await findTestResultFiles(basePath, modules);
        info(`Found ${testFiles.length} test result files`);

        for (const file of testFiles.slice(0, 10)) {
            info(`  - ${file}`);
        }
        if (testFiles.length > 10) {
            info(`  ... and ${testFiles.length - 10} more files`);
        }

        endGroup();

        startGroup('Aggregating Test Results');

        // Aggregate results
        const results = await aggregateTestResults(testFiles);
        const testResult = toTestResult(groupName, results);

        info(`Total Tests: ${results.totalTests}`);
        info(`Failed: ${results.failedTests}`);
        info(`Errors: ${results.errorTests}`);
        info(`Skipped: ${results.skippedTests}`);
        info(`Duration: ${results.duration.toFixed(2)}s`);

        endGroup();

        // Set outputs
        setOutput('passed', testResult.passed);
        setOutput('total-tests', testResult.totalTests);
        setOutput('failed-tests', testResult.failedTests);
        setOutput('skipped-tests', testResult.skippedTests);

        // Generate summary
        const summary = generateTestSummary(groupName, modules, results, useTestContainers);
        setOutput('summary', summary.substring(0, 1000)); // Truncate for output

        // Write step summary
        await writeStepSummary(summary);

        // Report result
        if (testResult.passed) {
            info(`✅ All tests passed for ${groupName}`);
        } else {
            logError(`❌ Tests failed for ${groupName}: ${testResult.failedTests} failures`);

            // Note: We don't fail the action here, let the workflow decide
            // based on the 'passed' output
        }

        // Note: Artifact upload is handled by the workflow using actions/upload-artifact
        // We just provide the information about what to upload
        if (uploadArtifacts && testFiles.length > 0) {
            info('Test result files are available for artifact upload');
            // The workflow should use actions/upload-artifact with the test-results path
        }
    } catch (err) {
        if (err instanceof Error) {
            setFailed(`Action failed: ${err.message}`);
        } else {
            setFailed('Action failed with unknown error');
        }
    }
}

// Run the action
run();
