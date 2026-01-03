/**
 * Property-based tests for Test Matrix Configuration
 *
 * **Feature: github-actions-typescript, Property 5: Test Matrix Schema Validation**
 * **Validates: Requirements 5.2, 5.3**
 */

import { describe, it, expect } from 'vitest';
import fc from 'fast-check';
import {
    TEST_MATRIX,
    getTestGroup,
    getTestContainersGroups,
    getLightweightGroups,
    getTotalModuleCount,
    toGitHubMatrix,
} from './test-matrix.js';

describe('Property 5: Test Matrix Schema Validation', () => {
    /**
     * Property: For any entry in the TEST_MATRIX configuration, it should have:
     * 1. A non-empty `name` string
     * 2. A non-empty `modules` array
     * 3. A boolean `testcontainers` field
     * 4. A positive number `timeout` field
     */
    it('should have valid schema for all test groups', () => {
        fc.assert(
            fc.property(fc.constantFrom(...TEST_MATRIX), (group) => {
                // 1. Non-empty name string
                expect(typeof group.name).toBe('string');
                expect(group.name.length).toBeGreaterThan(0);

                // 2. Non-empty modules array
                expect(Array.isArray(group.modules)).toBe(true);
                expect(group.modules.length).toBeGreaterThan(0);

                // 3. Boolean testcontainers field
                expect(typeof group.testcontainers).toBe('boolean');

                // 4. Positive number timeout field
                expect(typeof group.timeout).toBe('number');
                expect(group.timeout).toBeGreaterThan(0);
            }),
            { numRuns: 100 }
        );
    });

    it('should have unique group names', () => {
        const names = TEST_MATRIX.map((g) => g.name);
        const uniqueNames = new Set(names);
        expect(uniqueNames.size).toBe(names.length);
    });

    it('should have all modules as non-empty strings', () => {
        fc.assert(
            fc.property(fc.constantFrom(...TEST_MATRIX), (group) => {
                for (const module of group.modules) {
                    expect(typeof module).toBe('string');
                    expect(module.length).toBeGreaterThan(0);
                }
            }),
            { numRuns: 100 }
        );
    });

    it('should have exactly 12 test groups as specified', () => {
        expect(TEST_MATRIX.length).toBe(12);
    });

    it('should have reasonable timeout values (between 5 and 60 minutes)', () => {
        fc.assert(
            fc.property(fc.constantFrom(...TEST_MATRIX), (group) => {
                expect(group.timeout).toBeGreaterThanOrEqual(5);
                expect(group.timeout).toBeLessThanOrEqual(60);
            }),
            { numRuns: 100 }
        );
    });

    /**
     * Property: getTestGroup should return the correct group or undefined
     */
    it('should find existing groups by name', () => {
        fc.assert(
            fc.property(fc.constantFrom(...TEST_MATRIX), (group) => {
                const found = getTestGroup(group.name);
                expect(found).toBeDefined();
                expect(found?.name).toBe(group.name);
                expect(found?.modules).toEqual(group.modules);
            }),
            { numRuns: 100 }
        );
    });

    it('should return undefined for non-existent groups', () => {
        fc.assert(
            fc.property(
                fc.string({ minLength: 1, maxLength: 50 }).filter(
                    (s) => !TEST_MATRIX.some((g) => g.name === s)
                ),
                (nonExistentName) => {
                    expect(getTestGroup(nonExistentName)).toBeUndefined();
                }
            ),
            { numRuns: 100 }
        );
    });

    /**
     * Property: TestContainers groups should be correctly filtered
     */
    it('should correctly partition groups by testcontainers requirement', () => {
        const tcGroups = getTestContainersGroups();
        const lightGroups = getLightweightGroups();

        // All TC groups should have testcontainers: true
        for (const group of tcGroups) {
            expect(group.testcontainers).toBe(true);
        }

        // All lightweight groups should have testcontainers: false
        for (const group of lightGroups) {
            expect(group.testcontainers).toBe(false);
        }

        // Together they should equal the total
        expect(tcGroups.length + lightGroups.length).toBe(TEST_MATRIX.length);
    });

    /**
     * Property: Total module count should be consistent
     */
    it('should correctly calculate total module count', () => {
        const expectedTotal = TEST_MATRIX.reduce((sum, g) => sum + g.modules.length, 0);
        expect(getTotalModuleCount()).toBe(expectedTotal);
    });

    /**
     * Property: GitHub matrix format should be valid
     */
    it('should generate valid GitHub Actions matrix format', () => {
        const matrix = toGitHubMatrix();

        expect(matrix).toHaveProperty('include');
        expect(Array.isArray(matrix.include)).toBe(true);
        expect(matrix.include.length).toBe(TEST_MATRIX.length);

        fc.assert(
            fc.property(fc.constantFrom(...matrix.include), (entry) => {
                expect(typeof entry.name).toBe('string');
                expect(typeof entry.modules).toBe('string');
                expect(typeof entry.testcontainers).toBe('boolean');
                expect(typeof entry.timeout).toBe('number');
            }),
            { numRuns: 100 }
        );
    });

    it('should have modules as space-separated string in GitHub matrix', () => {
        const matrix = toGitHubMatrix();

        for (let i = 0; i < TEST_MATRIX.length; i++) {
            const original = TEST_MATRIX[i];
            const converted = matrix.include[i];

            expect(converted.modules).toBe(original.modules.join(' '));
        }
    });
});
