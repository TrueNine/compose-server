/**
 * Property-based tests for Version Utils
 *
 * **Feature: github-actions-typescript, Property 4: Semver Validation**
 * **Validates: Requirements 4.1, 4.3**
 */

import { describe, it, expect } from 'vitest';
import fc from 'fast-check';
import { isValidSemver, isPrerelease, parseVersion, compareSemver } from './version-utils.js';

describe('Property 4: Semver Validation', () => {
    /**
     * Property: For any version string, the validator should correctly identify
     * valid semver format (x.y.z with optional prerelease/build metadata)
     */
    it('should validate standard semver format (major.minor.patch)', () => {
        fc.assert(
            fc.property(
                fc.integer({ min: 0, max: 999 }),
                fc.integer({ min: 0, max: 999 }),
                fc.integer({ min: 0, max: 999 }),
                (major, minor, patch) => {
                    const version = `${major}.${minor}.${patch}`;
                    expect(isValidSemver(version)).toBe(true);
                }
            ),
            { numRuns: 100 }
        );
    });

    it('should validate semver with v prefix', () => {
        fc.assert(
            fc.property(
                fc.integer({ min: 0, max: 999 }),
                fc.integer({ min: 0, max: 999 }),
                fc.integer({ min: 0, max: 999 }),
                (major, minor, patch) => {
                    const version = `v${major}.${minor}.${patch}`;
                    expect(isValidSemver(version)).toBe(true);
                }
            ),
            { numRuns: 100 }
        );
    });

    it('should validate semver with prerelease identifiers', () => {
        const prereleaseArb = fc.oneof(
            fc.constant('alpha'),
            fc.constant('beta'),
            fc.constant('rc'),
            fc.constant('SNAPSHOT'),
            fc.integer({ min: 1, max: 99 }).map((n) => `alpha.${n}`),
            fc.integer({ min: 1, max: 99 }).map((n) => `beta.${n}`),
            fc.integer({ min: 1, max: 99 }).map((n) => `rc.${n}`)
        );

        fc.assert(
            fc.property(
                fc.integer({ min: 0, max: 99 }),
                fc.integer({ min: 0, max: 99 }),
                fc.integer({ min: 0, max: 99 }),
                prereleaseArb,
                (major, minor, patch, prerelease) => {
                    const version = `${major}.${minor}.${patch}-${prerelease}`;
                    expect(isValidSemver(version)).toBe(true);
                }
            ),
            { numRuns: 100 }
        );
    });

    it('should reject invalid version formats', () => {
        const invalidArb = fc.oneof(
            // Missing components
            fc.integer({ min: 0, max: 99 }).map((n) => `${n}`),
            fc.tuple(fc.integer({ min: 0, max: 99 }), fc.integer({ min: 0, max: 99 })).map(
                ([a, b]) => `${a}.${b}`
            ),
            // Non-numeric components
            fc.constant('a.b.c'),
            fc.constant('1.2.x'),
            // Empty or whitespace
            fc.constant(''),
            fc.constant('   ')
        );

        fc.assert(
            fc.property(invalidArb, (invalid) => {
                expect(isValidSemver(invalid)).toBe(false);
            }),
            { numRuns: 100 }
        );
    });

    /**
     * Property: Prerelease versions should be correctly identified
     */
    it('should identify prerelease versions (SNAPSHOT, alpha, beta, rc)', () => {
        const prereleaseVersionArb = fc.oneof(
            fc.tuple(
                fc.integer({ min: 0, max: 99 }),
                fc.integer({ min: 0, max: 99 }),
                fc.integer({ min: 0, max: 99 })
            ).map(([a, b, c]) => `${a}.${b}.${c}-SNAPSHOT`),
            fc.tuple(
                fc.integer({ min: 0, max: 99 }),
                fc.integer({ min: 0, max: 99 }),
                fc.integer({ min: 0, max: 99 })
            ).map(([a, b, c]) => `${a}.${b}.${c}-alpha`),
            fc.tuple(
                fc.integer({ min: 0, max: 99 }),
                fc.integer({ min: 0, max: 99 }),
                fc.integer({ min: 0, max: 99 })
            ).map(([a, b, c]) => `${a}.${b}.${c}-beta`),
            fc.tuple(
                fc.integer({ min: 0, max: 99 }),
                fc.integer({ min: 0, max: 99 }),
                fc.integer({ min: 0, max: 99 })
            ).map(([a, b, c]) => `${a}.${b}.${c}-rc`),
            fc.tuple(
                fc.integer({ min: 0, max: 99 }),
                fc.integer({ min: 0, max: 99 }),
                fc.integer({ min: 0, max: 99 }),
                fc.integer({ min: 1, max: 9 })
            ).map(([a, b, c, n]) => `${a}.${b}.${c}-alpha.${n}`),
            fc.tuple(
                fc.integer({ min: 0, max: 99 }),
                fc.integer({ min: 0, max: 99 }),
                fc.integer({ min: 0, max: 99 }),
                fc.integer({ min: 1, max: 9 })
            ).map(([a, b, c, n]) => `${a}.${b}.${c}-M${n}`)
        );

        fc.assert(
            fc.property(prereleaseVersionArb, (version) => {
                expect(isPrerelease(version)).toBe(true);
            }),
            { numRuns: 100 }
        );
    });

    it('should identify stable versions as non-prerelease', () => {
        fc.assert(
            fc.property(
                fc.integer({ min: 0, max: 99 }),
                fc.integer({ min: 0, max: 99 }),
                fc.integer({ min: 0, max: 99 }),
                (major, minor, patch) => {
                    const version = `${major}.${minor}.${patch}`;
                    expect(isPrerelease(version)).toBe(false);
                }
            ),
            { numRuns: 100 }
        );
    });

    /**
     * Property: parseVersion should normalize versions correctly
     */
    it('should normalize versions by removing v prefix', () => {
        fc.assert(
            fc.property(
                fc.integer({ min: 0, max: 99 }),
                fc.integer({ min: 0, max: 99 }),
                fc.integer({ min: 0, max: 99 }),
                fc.boolean(),
                (major, minor, patch, hasPrefix) => {
                    const input = hasPrefix
                        ? `v${major}.${minor}.${patch}`
                        : `${major}.${minor}.${patch}`;
                    const expected = `${major}.${minor}.${patch}`;
                    expect(parseVersion(input)).toBe(expected);
                }
            ),
            { numRuns: 100 }
        );
    });

    /**
     * Property: Version comparison should be transitive and consistent
     */
    it('should compare versions correctly (higher version > lower version)', () => {
        fc.assert(
            fc.property(
                fc.integer({ min: 0, max: 99 }),
                fc.integer({ min: 0, max: 99 }),
                fc.integer({ min: 0, max: 99 }),
                fc.integer({ min: 1, max: 10 }),
                (major, minor, patch, increment) => {
                    const lower = `${major}.${minor}.${patch}`;
                    const higher = `${major}.${minor}.${patch + increment}`;
                    expect(compareSemver(lower, higher)).toBe(-1);
                    expect(compareSemver(higher, lower)).toBe(1);
                    expect(compareSemver(lower, lower)).toBe(0);
                }
            ),
            { numRuns: 100 }
        );
    });

    it('should rank prerelease versions lower than stable versions', () => {
        fc.assert(
            fc.property(
                fc.integer({ min: 0, max: 99 }),
                fc.integer({ min: 0, max: 99 }),
                fc.integer({ min: 0, max: 99 }),
                (major, minor, patch) => {
                    const stable = `${major}.${minor}.${patch}`;
                    const prerelease = `${major}.${minor}.${patch}-alpha`;
                    expect(compareSemver(prerelease, stable)).toBe(-1);
                    expect(compareSemver(stable, prerelease)).toBe(1);
                }
            ),
            { numRuns: 100 }
        );
    });
});
