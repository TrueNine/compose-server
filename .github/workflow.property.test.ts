/**
 * Property-based tests for Workflow File Simplicity
 *
 * **Feature: github-actions-typescript, Property 10: Workflow File Simplicity**
 * **Validates: Requirements 10.3, 10.4**
 */

import { describe, it, expect } from 'vitest';
import fc from 'fast-check';
import * as fs from 'node:fs';
import * as path from 'node:path';

const workflowsDir = path.join(import.meta.dirname, 'workflows');

/**
 * Get all workflow files in .github/workflows/
 */
function getWorkflowFiles(): string[] {
    if (!fs.existsSync(workflowsDir)) {
        return [];
    }

    return fs
        .readdirSync(workflowsDir, { withFileTypes: true })
        .filter((file) => file.isFile())
        .filter((file) => file.name.endsWith('.yaml') || file.name.endsWith('.yml'))
        .map((file) => file.name);
}

describe('Property 10: Workflow File Simplicity', () => {
    const workflowFiles = getWorkflowFiles();

    /**
     * Property: For any workflow file in .github/workflows/,
     * line count should be under 100 lines
     *
     * This validates Requirement 10.3: THE Workflow_Files SHALL be under 100 lines each
     */
    it('should have workflow files under 100 lines', () => {
        fc.assert(
            fc.property(fc.constantFrom(...workflowFiles), (workflowFile) => {
                const filePath = path.join(workflowsDir, workflowFile);

                // File should exist
                expect(fs.existsSync(filePath)).toBe(true);

                // Count lines
                const content = fs.readFileSync(filePath, 'utf-8');
                const lineCount = content.split('\n').length;

                // Should be under 100 lines
                expect(lineCount).toBeLessThanOrEqual(100);

                return true;
            }),
            { numRuns: Math.max(workflowFiles.length * 10, 100) }
        );
    });

    /**
     * Property: For any workflow file that uses custom actions,
     * all action references should use local .github/actions/ path
     *
     * This validates Requirement 10.4: THE Workflow_Files SHALL reference actions from `.github/actions/` directory
     */
    it('should reference local actions from .github/actions/', () => {
        fc.assert(
            fc.property(fc.constantFrom(...workflowFiles), (workflowFile) => {
                const filePath = path.join(workflowsDir, workflowFile);
                const content = fs.readFileSync(filePath, 'utf-8');

                // Find all uses: directives that reference local actions
                const localActionPattern = /uses:\s*\.\/\.github\/actions\/([a-z-]+)/g;
                const matches = content.matchAll(localActionPattern);

                for (const match of matches) {
                    const actionName = match[1];
                    const actionPath = path.join(import.meta.dirname, 'actions', actionName);

                    // Referenced action should exist
                    expect(fs.existsSync(actionPath)).toBe(true);

                    // Action should have action.yml
                    const actionYmlPath = path.join(actionPath, 'action.yml');
                    expect(fs.existsSync(actionYmlPath)).toBe(true);
                }

                return true;
            }),
            { numRuns: Math.max(workflowFiles.length * 10, 100) }
        );
    });

    /**
     * Property: Workflow files should use custom actions instead of inline shell scripts
     * for complex operations (version extraction, maven checks, etc.)
     *
     * This validates Requirements 10.1, 10.2: THE CI_Workflow and Publish_Workflow
     * SHALL use custom actions instead of inline shell scripts
     */
    it('should use custom actions for complex operations', () => {
        fc.assert(
            fc.property(fc.constantFrom(...workflowFiles), (workflowFile) => {
                const filePath = path.join(workflowsDir, workflowFile);
                const content = fs.readFileSync(filePath, 'utf-8');

                // Check that workflow uses local custom actions
                const usesLocalActions = content.includes('uses: ./.github/actions/');

                // If workflow has complex operations, it should use custom actions
                // Complex operations include: version extraction, maven checks, test collection
                const hasComplexOperations =
                    content.includes('extract') ||
                    content.includes('maven') ||
                    content.includes('version-check') ||
                    content.includes('test-collector') ||
                    content.includes('publish-report');

                // If it has complex operations, it should use local actions
                if (hasComplexOperations) {
                    expect(usesLocalActions).toBe(true);
                }

                return true;
            }),
            { numRuns: Math.max(workflowFiles.length * 10, 100) }
        );
    });

    /**
     * Property: All workflow files should be valid YAML
     */
    it('should be valid YAML files', () => {
        fc.assert(
            fc.property(fc.constantFrom(...workflowFiles), (workflowFile) => {
                const filePath = path.join(workflowsDir, workflowFile);
                const content = fs.readFileSync(filePath, 'utf-8');

                // Basic YAML structure checks
                // Should have a name field
                expect(content).toMatch(/^name:/m);

                // Should have an on: trigger
                expect(content).toMatch(/^on:/m);

                // Should have jobs:
                expect(content).toMatch(/^jobs:/m);

                return true;
            }),
            { numRuns: Math.max(workflowFiles.length * 10, 100) }
        );
    });
});
