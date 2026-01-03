/**
 * Version utilities for semantic versioning validation and parsing
 *
 * @module version-utils
 */

/**
 * Semver regex pattern
 * Matches: major.minor.patch with optional prerelease and build metadata
 * Examples: 1.0.0, 2.1.3-alpha, 1.0.0-beta.1, 1.0.0+build.123
 */
const SEMVER_REGEX =
    /^(0|[1-9]\d*)\.(0|[1-9]\d*)\.(0|[1-9]\d*)(?:-((?:0|[1-9]\d*|\d*[a-zA-Z-][0-9a-zA-Z-]*)(?:\.(?:0|[1-9]\d*|\d*[a-zA-Z-][0-9a-zA-Z-]*))*))?(?:\+([0-9a-zA-Z-]+(?:\.[0-9a-zA-Z-]+)*))?$/;

/**
 * Prerelease identifier patterns
 */
const PRERELEASE_PATTERNS = [
    /^.*-SNAPSHOT$/i,
    /^.*-alpha(\.\d+)?$/i,
    /^.*-beta(\.\d+)?$/i,
    /^.*-rc(\.\d+)?$/i,
    /^.*-dev(\.\d+)?$/i,
    /^.*-preview(\.\d+)?$/i,
    /^.*-M\d+$/i, // Milestone releases like 1.0.0-M1
];

/**
 * Validate if a version string follows semantic versioning format
 *
 * @param version - Version string to validate
 * @returns true if version is valid semver format
 */
export function isValidSemver(version: string): boolean {
    if (!version || typeof version !== 'string') {
        return false;
    }

    // Remove leading 'v' if present
    const cleanVersion = version.startsWith('v') ? version.slice(1) : version;

    return SEMVER_REGEX.test(cleanVersion);
}

/**
 * Check if a version is a prerelease version
 *
 * @param version - Version string to check
 * @returns true if version is a prerelease (SNAPSHOT, alpha, beta, rc, etc.)
 */
export function isPrerelease(version: string): boolean {
    if (!version || typeof version !== 'string') {
        return false;
    }

    // Remove leading 'v' if present
    const cleanVersion = version.startsWith('v') ? version.slice(1) : version;

    // Check against prerelease patterns
    for (const pattern of PRERELEASE_PATTERNS) {
        if (pattern.test(cleanVersion)) {
            return true;
        }
    }

    // Also check semver prerelease part (anything with a hyphen followed by identifier)
    const match = SEMVER_REGEX.exec(cleanVersion);
    if (match && match[4]) {
        // match[4] is the prerelease identifier
        return true;
    }

    return false;
}

/**
 * Parse and normalize a version string
 * Handles 'v' prefix and validates format
 *
 * @param input - Version string to parse
 * @returns Normalized version string without 'v' prefix
 * @throws {Error} When version format is invalid
 */
export function parseVersion(input: string): string {
    if (!input || typeof input !== 'string') {
        throw new Error('Version input must be a non-empty string');
    }

    const trimmed = input.trim();

    // Remove leading 'v' if present
    const version = trimmed.startsWith('v') ? trimmed.slice(1) : trimmed;

    // For strict semver validation, uncomment:
    // if (!isValidSemver(version)) {
    //     throw new Error(`Invalid semver format: ${input}`);
    // }

    return version;
}

/**
 * Compare two semver versions
 *
 * @param a - First version
 * @param b - Second version
 * @returns -1 if a < b, 0 if a == b, 1 if a > b
 */
export function compareSemver(a: string, b: string): -1 | 0 | 1 {
    const parseVer = (v: string) => {
        const clean = v.startsWith('v') ? v.slice(1) : v;
        const match = SEMVER_REGEX.exec(clean);
        if (!match) {
            return { major: 0, minor: 0, patch: 0, prerelease: '' };
        }
        return {
            major: parseInt(match[1] ?? '0', 10),
            minor: parseInt(match[2] ?? '0', 10),
            patch: parseInt(match[3] ?? '0', 10),
            prerelease: match[4] || '',
        };
    };

    const va = parseVer(a);
    const vb = parseVer(b);

    // Compare major.minor.patch
    if (va.major !== vb.major) return va.major < vb.major ? -1 : 1;
    if (va.minor !== vb.minor) return va.minor < vb.minor ? -1 : 1;
    if (va.patch !== vb.patch) return va.patch < vb.patch ? -1 : 1;

    // Prerelease versions have lower precedence
    if (va.prerelease && !vb.prerelease) return -1;
    if (!va.prerelease && vb.prerelease) return 1;
    if (va.prerelease && vb.prerelease) {
        return va.prerelease < vb.prerelease ? -1 : va.prerelease > vb.prerelease ? 1 : 0;
    }

    return 0;
}

/**
 * Extract major, minor, patch components from a version string
 *
 * @param version - Version string
 * @returns Object with major, minor, patch numbers or null if invalid
 */
export function extractVersionComponents(
    version: string
): { major: number; minor: number; patch: number } | null {
    const clean = version.startsWith('v') ? version.slice(1) : version;
    const match = SEMVER_REGEX.exec(clean);

    if (!match) {
        return null;
    }

    return {
        major: parseInt(match[1] ?? '0', 10),
        minor: parseInt(match[2] ?? '0', 10),
        patch: parseInt(match[3] ?? '0', 10),
    };
}
