/**
 * Property-based tests for Gradle Exec Action
 *
 * **Feature: github-actions-typescript, Property 6: Gradle Execution Output Capture**
 * **Validates: Requirements 6.4, 6.5**
 */

import { describe, it, expect } from 'vitest';
import fc from 'fast-check';
import { buildGradleOpts, buildGradleCommand } from './index.js';
import type { GradleExecOptions } from '@github-actions/shared';

describe('Property 6: Gradle Execution Output Capture', () => {
    /**
     * Property: For any valid GradleExecOptions, buildGradleCommand should produce
     * a command array that includes all specified tasks
     */
    it('should include all tasks in the generated command', () => {
        const taskArb = fc.stringMatching(/^[a-zA-Z][a-zA-Z0-9:]*$/);
        const tasksArb = fc.array(taskArb, { minLength: 1, maxLength: 5 });

        fc.assert(
            fc.property(tasksArb, (tasks) => {
                const options: GradleExecOptions = { tasks };
                const command = buildGradleCommand(options);

                // All tasks should be present in the command
                for (const task of tasks) {
                    expect(command).toContain(task);
                }
            }),
            { numRuns: 100 }
        );
    });

    /**
     * Property: When parallel is enabled, the command should include --parallel flag
     */
    it('should include --parallel flag when parallel is enabled', () => {
        const taskArb = fc.stringMatching(/^[a-zA-Z][a-zA-Z0-9:]*$/);
        const tasksArb = fc.array(taskArb, { minLength: 1, maxLength: 3 });

        fc.assert(
            fc.property(tasksArb, (tasks) => {
                const options: GradleExecOptions = { tasks, parallel: true };
                const command = buildGradleCommand(options);
                expect(command).toContain('--parallel');
            }),
            { numRuns: 100 }
        );
    });

    /**
     * Property: When parallel is disabled, the command should NOT include --parallel flag
     */
    it('should NOT include --parallel flag when parallel is disabled', () => {
        const taskArb = fc.stringMatching(/^[a-zA-Z][a-zA-Z0-9:]*$/);
        const tasksArb = fc.array(taskArb, { minLength: 1, maxLength: 3 });

        fc.assert(
            fc.property(tasksArb, (tasks) => {
                const options: GradleExecOptions = { tasks, parallel: false };
                const command = buildGradleCommand(options);
                expect(command).not.toContain('--parallel');
            }),
            { numRuns: 100 }
        );
    });

    /**
     * Property: When build cache is enabled, the command should include --build-cache flag
     */
    it('should include --build-cache flag when buildCache is enabled', () => {
        const taskArb = fc.stringMatching(/^[a-zA-Z][a-zA-Z0-9:]*$/);
        const tasksArb = fc.array(taskArb, { minLength: 1, maxLength: 3 });

        fc.assert(
            fc.property(tasksArb, (tasks) => {
                const options: GradleExecOptions = { tasks, buildCache: true };
                const command = buildGradleCommand(options);
                expect(command).toContain('--build-cache');
            }),
            { numRuns: 100 }
        );
    });

    /**
     * Property: When configuration cache is enabled, the command should include --configuration-cache flag
     */
    it('should include --configuration-cache flag when configurationCache is enabled', () => {
        const taskArb = fc.stringMatching(/^[a-zA-Z][a-zA-Z0-9:]*$/);
        const tasksArb = fc.array(taskArb, { minLength: 1, maxLength: 3 });

        fc.assert(
            fc.property(tasksArb, (tasks) => {
                const options: GradleExecOptions = { tasks, configurationCache: true };
                const command = buildGradleCommand(options);
                expect(command).toContain('--configuration-cache');
            }),
            { numRuns: 100 }
        );
    });

    /**
     * Property: When maxWorkers is specified, the command should include --max-workers flag
     */
    it('should include --max-workers flag with correct value when specified', () => {
        const taskArb = fc.stringMatching(/^[a-zA-Z][a-zA-Z0-9:]*$/);
        const tasksArb = fc.array(taskArb, { minLength: 1, maxLength: 3 });
        const workersArb = fc.integer({ min: 1, max: 16 });

        fc.assert(
            fc.property(tasksArb, workersArb, (tasks, maxWorkers) => {
                const options: GradleExecOptions = { tasks, maxWorkers };
                const command = buildGradleCommand(options);
                expect(command).toContain(`--max-workers=${maxWorkers}`);
            }),
            { numRuns: 100 }
        );
    });

    /**
     * Property: Command should always include --stacktrace for better error reporting
     */
    it('should always include --stacktrace flag', () => {
        const taskArb = fc.stringMatching(/^[a-zA-Z][a-zA-Z0-9:]*$/);
        const tasksArb = fc.array(taskArb, { minLength: 1, maxLength: 3 });

        fc.assert(
            fc.property(tasksArb, (tasks) => {
                const options: GradleExecOptions = { tasks };
                const command = buildGradleCommand(options);
                expect(command).toContain('--stacktrace');
            }),
            { numRuns: 100 }
        );
    });

    /**
     * Property: buildGradleOpts should always include daemon=false for CI
     */
    it('should always disable daemon in GRADLE_OPTS', () => {
        const optionsArb = fc.record({
            parallel: fc.boolean(),
            buildCache: fc.boolean(),
            maxWorkers: fc.option(fc.integer({ min: 1, max: 16 }), { nil: undefined }),
        });

        fc.assert(
            fc.property(optionsArb, (options) => {
                const opts = buildGradleOpts(options);
                expect(opts).toContain('-Dorg.gradle.daemon=false');
            }),
            { numRuns: 100 }
        );
    });

    /**
     * Property: buildGradleOpts should include parallel setting based on option
     */
    it('should include parallel setting in GRADLE_OPTS when enabled', () => {
        fc.assert(
            fc.property(fc.constant(true), () => {
                const opts = buildGradleOpts({ parallel: true });
                expect(opts).toContain('-Dorg.gradle.parallel=true');
            }),
            { numRuns: 100 }
        );
    });

    /**
     * Property: buildGradleOpts should include caching setting based on option
     */
    it('should include caching setting in GRADLE_OPTS when enabled', () => {
        fc.assert(
            fc.property(fc.constant(true), () => {
                const opts = buildGradleOpts({ buildCache: true });
                expect(opts).toContain('-Dorg.gradle.caching=true');
            }),
            { numRuns: 100 }
        );
    });

    /**
     * Property: buildGradleOpts should include max workers when specified
     */
    it('should include max workers in GRADLE_OPTS when specified', () => {
        const workersArb = fc.integer({ min: 1, max: 16 });

        fc.assert(
            fc.property(workersArb, (maxWorkers) => {
                const opts = buildGradleOpts({ maxWorkers });
                expect(opts).toContain(`-Dorg.gradle.workers.max=${maxWorkers}`);
            }),
            { numRuns: 100 }
        );
    });

    /**
     * Property: buildGradleOpts should include custom JVM args when provided
     */
    it('should include custom JVM args in GRADLE_OPTS when provided', () => {
        const jvmArgsArb = fc.stringMatching(/^-X[a-zA-Z0-9]+$/);

        fc.assert(
            fc.property(jvmArgsArb, (jvmArgs) => {
                const opts = buildGradleOpts({ jvmArgs });
                expect(opts).toContain(jvmArgs);
            }),
            { numRuns: 100 }
        );
    });

    /**
     * Property: For any combination of options, the generated command should be a valid array
     */
    it('should generate valid command array for any option combination', () => {
        const taskArb = fc.stringMatching(/^[a-zA-Z][a-zA-Z0-9:]*$/);
        const tasksArb = fc.array(taskArb, { minLength: 1, maxLength: 5 });
        const optionsArb = fc.record({
            tasks: tasksArb,
            parallel: fc.option(fc.boolean(), { nil: undefined }),
            buildCache: fc.option(fc.boolean(), { nil: undefined }),
            configurationCache: fc.option(fc.boolean(), { nil: undefined }),
            maxWorkers: fc.option(fc.integer({ min: 1, max: 16 }), { nil: undefined }),
            timeout: fc.option(fc.integer({ min: 1, max: 120 }), { nil: undefined }),
        });

        fc.assert(
            fc.property(optionsArb, (options) => {
                const command = buildGradleCommand(options as GradleExecOptions);

                // Command should be a non-empty array
                expect(Array.isArray(command)).toBe(true);
                expect(command.length).toBeGreaterThan(0);

                // All elements should be strings
                for (const arg of command) {
                    expect(typeof arg).toBe('string');
                }
            }),
            { numRuns: 100 }
        );
    });
});
