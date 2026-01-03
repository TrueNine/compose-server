/**
 * TOML Parser for extracting version information from libs.versions.toml
 *
 * @module toml-parser
 */

import { parse as parseTomlContent } from '@iarna/toml';
import { readFile } from 'node:fs/promises';
import type { VersionInfo } from './types.js';
import { TomlParseError } from './types.js';

/**
 * Parse TOML content string into an object
 *
 * @param content - Raw TOML content string
 * @returns Parsed TOML object
 * @throws {TomlParseError} When TOML content is malformed
 */
export function parseToml(content: string): Record<string, unknown> {
    if (!content || content.trim().length === 0) {
        throw new TomlParseError('TOML content is empty');
    }

    try {
        return parseTomlContent(content) as Record<string, unknown>;
    } catch (error) {
        if (error instanceof Error) {
            // Extract line/column info from @iarna/toml error messages
            const lineMatch = error.message.match(/line (\d+)/i);
            const colMatch = error.message.match(/col(?:umn)? (\d+)/i);
            const line = lineMatch?.[1] ? parseInt(lineMatch[1], 10) : undefined;
            const col = colMatch?.[1] ? parseInt(colMatch[1], 10) : undefined;
            throw new TomlParseError(error.message, line, col);
        }
        throw new TomlParseError('Unknown parsing error');
    }
}

/**
 * Extract version information from parsed TOML object
 *
 * @param toml - Parsed TOML object
 * @returns Version information object
 * @throws {TomlParseError} When required version fields are missing
 */
export function extractVersionsFromToml(toml: Record<string, unknown>): VersionInfo {
    const versions = toml.versions as Record<string, string> | undefined;

    if (!versions || typeof versions !== 'object') {
        throw new TomlParseError('Missing [versions] section in TOML');
    }

    const java = versions['java'];
    const gradle = versions['org-gradle'];
    const project = versions['project'];

    const missing: string[] = [];
    if (!java) missing.push('java');
    if (!gradle) missing.push('org-gradle');
    if (!project) missing.push('project');

    if (missing.length > 0) {
        throw new TomlParseError(`Missing required version fields: ${missing.join(', ')}`);
    }

    return {
        java: String(java),
        gradle: String(gradle),
        project: String(project),
    };
}

/**
 * Extract version information from a TOML file path
 *
 * @param tomlPath - Path to the libs.versions.toml file
 * @returns Version information object
 * @throws {TomlParseError} When file cannot be read or parsed
 */
export async function extractVersions(tomlPath: string): Promise<VersionInfo> {
    let content: string;

    try {
        content = await readFile(tomlPath, 'utf-8');
    } catch (error) {
        if (error instanceof Error) {
            throw new TomlParseError(`Failed to read TOML file: ${error.message}`);
        }
        throw new TomlParseError('Failed to read TOML file');
    }

    const toml = parseToml(content);
    return extractVersionsFromToml(toml);
}
