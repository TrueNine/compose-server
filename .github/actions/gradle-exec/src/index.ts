/**
 * Gradle Execute Action
 *
 * Executes Gradle commands with consistent configuration including
 * parallel execution, build cache, configuration cache, and timeout handling.
 *
 * @module gradle-exec
 */

import * as exec from '@actions/exec';
import type { GradleExecOptions } from '@github-actions/shared';
import {
    GradleExecError,
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
} from '@github-actions/shared';



/**
 * Build GRADLE_OPTS environment variable string
 *
 * @param options - Gradle execution options
 * @returns GRADLE_OPTS string
 */
export function buildGradleOpts(options: Partial<GradleExecOptions>): string {
    const opts: string[] = [
        '-Dorg.gradle.daemon=false',
        '-Dorg.gradle.console=plain',
        '-Dkotlin.incremental=false',
    ];

    if (options.parallel !== false) {
        opts.push('-Dorg.gradle.parallel=true');
    }

    if (options.buildCache !== false) {
        opts.push('-Dorg.gradle.caching=true');
    }

    if (options.maxWorkers !== undefined && options.maxWorkers > 0) {
        opts.push(`-Dorg.gradle.workers.max=${options.maxWorkers}`);
    }

    if (options.jvmArgs) {
        opts.push(options.jvmArgs);
    }

    return opts.join(' ');
}

/**
 * Build Gradle command arguments
 *
 * @param options - Gradle execution options
 * @returns Array of command arguments
 */
export function buildGradleCommand(options: GradleExecOptions): string[] {
    const args: string[] = [...options.tasks];

    if (options.parallel !== false) {
        args.push('--parallel');
    }

    if (options.buildCache !== false) {
        args.push('--build-cache');
    }

    if (options.configurationCache !== false) {
        args.push('--configuration-cache');
    }

    if (options.maxWorkers !== undefined && options.maxWorkers > 0) {
        args.push(`--max-workers=${options.maxWorkers}`);
    }

    // Always add stacktrace for better error reporting
    args.push('--stacktrace');

    return args;
}

/**
 * Result of Gradle execution
 */
export interface GradleExecResult {
    /** Whether execution succeeded */
    success: boolean;
    /** Exit code */
    exitCode: number;
    /** Standard output */
    stdout: string;
    /** Standard error */
    stderr: string;
    /** Duration in milliseconds */
    duration: number;
}

/**
 * Execute Gradle command with timeout support
 *
 * @param options - Gradle execution options
 * @param workingDirectory - Working directory for execution
 * @returns Execution result
 */
export async function executeGradle(
    options: GradleExecOptions,
    workingDirectory: string = '.'
): Promise<GradleExecResult> {
    const args = buildGradleCommand(options);
    const gradleOpts = buildGradleOpts(options);

    let stdout = '';
    let stderr = '';
    const startTime = Date.now();

    // Determine the Gradle wrapper path
    const gradleWrapper = process.platform === 'win32' ? 'gradlew.bat' : './gradlew';

    info(`Executing: ${gradleWrapper} ${args.join(' ')}`);
    info(`GRADLE_OPTS: ${gradleOpts}`);
    info(`Working directory: ${workingDirectory}`);

    // Calculate timeout in milliseconds
    const timeoutMs = options.timeout !== undefined ? options.timeout * 60 * 1000 : 30 * 60 * 1000;

    // Create abort controller for timeout
    const controller = new AbortController();
    const timeoutId = setTimeout(() => {
        controller.abort();
    }, timeoutMs);

    try {
        const exitCode = await exec.exec(gradleWrapper, args, {
            cwd: workingDirectory,
            env: {
                ...Object.fromEntries(
                    Object.entries(process.env).filter(([, v]) => v !== undefined)
                ) as Record<string, string>,
                GRADLE_OPTS: gradleOpts,
            },
            listeners: {
                stdout: (data: Buffer) => {
                    stdout += data.toString();
                },
                stderr: (data: Buffer) => {
                    stderr += data.toString();
                },
            },
            ignoreReturnCode: true,
            silent: false,
        });

        clearTimeout(timeoutId);

        const duration = Date.now() - startTime;

        return {
            success: exitCode === 0,
            exitCode,
            stdout,
            stderr,
            duration,
        };
    } catch (err) {
        clearTimeout(timeoutId);

        const duration = Date.now() - startTime;

        // Check if it was a timeout
        if (controller.signal.aborted) {
            return {
                success: false,
                exitCode: -1,
                stdout,
                stderr: stderr + '\n[TIMEOUT] Gradle execution exceeded timeout limit',
                duration,
            };
        }

        // Other execution error
        return {
            success: false,
            exitCode: -1,
            stdout,
            stderr: stderr + `\n[ERROR] ${err instanceof Error ? err.message : 'Unknown error'}`,
            duration,
        };
    }
}

/**
 * Format duration in human-readable format
 *
 * @param ms - Duration in milliseconds
 * @returns Formatted duration string
 */
function formatDuration(ms: number): string {
    const seconds = Math.floor(ms / 1000);
    const minutes = Math.floor(seconds / 60);
    const remainingSeconds = seconds % 60;

    if (minutes > 0) {
        return `${minutes}m ${remainingSeconds}s`;
    }
    return `${seconds}s`;
}

/**
 * Generate step summary for Gradle execution
 *
 * @param result - Execution result
 * @param options - Gradle options used
 * @returns Markdown summary
 */
function generateSummary(result: GradleExecResult, options: GradleExecOptions): string {
    const statusIcon = result.success ? '✅' : '❌';
    const statusText = result.success ? 'Success' : 'Failed';

    let summary = `## ${statusIcon} Gradle Execution ${statusText}\n\n`;

    summary += generateMarkdownTable(
        ['Property', 'Value'],
        [
            ['Tasks', `\`${options.tasks.join(' ')}\``],
            ['Exit Code', `${result.exitCode}`],
            ['Duration', formatDuration(result.duration)],
            ['Parallel', options.parallel !== false ? 'Yes' : 'No'],
            ['Build Cache', options.buildCache !== false ? 'Yes' : 'No'],
            ['Configuration Cache', options.configurationCache !== false ? 'Yes' : 'No'],
            ['Max Workers', options.maxWorkers?.toString() ?? 'Default'],
        ]
    );

    // Add output details in collapsible sections
    if (result.stdout.trim()) {
        const truncatedStdout = result.stdout.length > 5000
            ? result.stdout.substring(result.stdout.length - 5000) + '\n... (truncated)'
            : result.stdout;
        summary += '\n\n' + generateCollapsible(
            '📋 Standard Output (last 5000 chars)',
            '```\n' + truncatedStdout + '\n```'
        );
    }

    if (result.stderr.trim()) {
        const truncatedStderr = result.stderr.length > 3000
            ? result.stderr.substring(result.stderr.length - 3000) + '\n... (truncated)'
            : result.stderr;
        summary += '\n\n' + generateCollapsible(
            '⚠️ Standard Error (last 3000 chars)',
            '```\n' + truncatedStderr + '\n```'
        );
    }

    // Add troubleshooting tips for failures
    if (!result.success) {
        summary += '\n\n### 🔍 Troubleshooting Tips\n\n';

        if (result.exitCode === -1 && result.stderr.includes('[TIMEOUT]')) {
            summary += '- **Timeout**: Consider increasing the timeout value or optimizing the build\n';
            summary += '- Check if tests are hanging or have infinite loops\n';
        } else {
            summary += '- Check the error output above for specific failure details\n';
            summary += '- Run `./gradlew ' + options.tasks.join(' ') + ' --stacktrace` locally for more details\n';
            summary += '- Ensure all dependencies are available and up to date\n';
        }
    }

    return summary;
}

/**
 * Parse input options from action inputs
 *
 * @returns Parsed GradleExecOptions
 */
function parseInputOptions(): GradleExecOptions {
    const tasksInput = getInput('tasks', true);
    const tasks = tasksInput.split(/\s+/).filter(t => t.length > 0);

    return {
        tasks,
        parallel: getBooleanInput('parallel'),
        buildCache: getBooleanInput('build-cache'),
        configurationCache: getBooleanInput('configuration-cache'),
        maxWorkers: parseInt(getInput('max-workers') || '4', 10),
        timeout: parseInt(getInput('timeout') || '30', 10),
    };
}

/**
 * Main entry point for the action
 */
async function run(): Promise<void> {
    try {
        // Parse inputs
        const options = parseInputOptions();
        const continueOnError = getBooleanInput('continue-on-error');
        const workingDirectory = getInput('working-directory') || '.';

        info(`Tasks: ${options.tasks.join(' ')}`);
        info(`Parallel: ${options.parallel}`);
        info(`Build Cache: ${options.buildCache}`);
        info(`Configuration Cache: ${options.configurationCache}`);
        info(`Max Workers: ${options.maxWorkers}`);
        info(`Timeout: ${options.timeout} minutes`);
        info(`Continue on Error: ${continueOnError}`);
        info(`Working Directory: ${workingDirectory}`);

        startGroup('Gradle Execution');

        // Execute Gradle
        const result = await executeGradle(options, workingDirectory);

        endGroup();

        // Set outputs
        setOutput('success', result.success);
        setOutput('exit-code', result.exitCode);

        // Truncate output for action output (GitHub has limits)
        const truncatedOutput = result.stdout.length > 10000
            ? result.stdout.substring(result.stdout.length - 10000)
            : result.stdout;
        setOutput('output', truncatedOutput);

        // Write step summary
        const summary = generateSummary(result, options);
        await writeStepSummary(summary);

        // Handle result
        if (result.success) {
            info(`✅ Gradle execution completed successfully in ${formatDuration(result.duration)}`);
        } else {
            const errorMessage = `Gradle execution failed with exit code ${result.exitCode}`;

            if (continueOnError) {
                warning(`⚠️ ${errorMessage} (continue-on-error enabled)`);
            } else {
                throw new GradleExecError(
                    errorMessage,
                    result.exitCode,
                    result.stdout,
                    result.stderr
                );
            }
        }
    } catch (err) {
        if (err instanceof GradleExecError) {
            logError(`Gradle failed with exit code ${err.exitCode}`);
            if (err.stderr) {
                logError('Error output:');
                logError(err.stderr.substring(0, 2000));
            }
            setFailed(err.message);
        } else if (err instanceof Error) {
            setFailed(`Action failed: ${err.message}`);
        } else {
            setFailed('Action failed with unknown error');
        }
    }
}

// Run the action
run();
