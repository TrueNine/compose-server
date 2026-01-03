/**
 * Publish Report Generator Action
 *
 * Generates publication reports for Maven Central publishing.
 * Supports three report types:
 * - pre-publish: Validation report before publishing
 * - summary: Publication summary after successful publish
 * - failure: Failure analysis report when errors occur
 *
 * @module publish-report
 */

import {
    setOutput,
    getInput,
    getBooleanInput,
    info,
    setFailed,
    writeStepSummary,
    generateMarkdownTable,
    generateCollapsible,
    getGitHubContext,
    isPrerelease,
} from '@github-actions/shared';

/**
 * Report type enumeration
 */
export type ReportType = 'pre-publish' | 'summary' | 'failure';

/**
 * Report generation options
 */
export interface ReportOptions {
    /** Type of report to generate */
    reportType: ReportType;
    /** Version being published */
    version: string;
    /** Maven group ID */
    groupId: string;
    /** List of artifacts */
    artifacts: string[];
    /** Whether publish was successful */
    publishSuccess: boolean;
    /** Error message for failure reports */
    errorMessage: string;
    /** Detailed error information */
    errorDetails: string;
    /** GitHub repository URL */
    repositoryUrl: string;
    /** GitHub release URL */
    releaseUrl: string;
}

/**
 * Generate Maven Central URL for an artifact
 *
 * @param groupId - Maven group ID
 * @param artifactId - Maven artifact ID
 * @param version - Version
 * @returns Maven Central URL
 */
export function getMavenCentralUrl(groupId: string, artifactId: string, version: string): string {
    const groupPath = groupId.replace(/\./g, '/');
    return `https://repo1.maven.org/maven2/${groupPath}/${artifactId}/${version}/`;
}

/**
 * Generate Maven Central search URL for an artifact
 *
 * @param groupId - Maven group ID
 * @param artifactId - Maven artifact ID
 * @returns Maven Central search URL
 */
export function getMavenSearchUrl(groupId: string, artifactId: string): string {
    return `https://central.sonatype.com/artifact/${groupId}/${artifactId}`;
}

/**
 * Generate Maven dependency snippet
 *
 * @param groupId - Maven group ID
 * @param artifactId - Maven artifact ID
 * @param version - Version
 * @returns Maven XML dependency snippet
 */
export function generateMavenDependency(groupId: string, artifactId: string, version: string): string {
    return `<dependency>
    <groupId>${groupId}</groupId>
    <artifactId>${artifactId}</artifactId>
    <version>${version}</version>
</dependency>`;
}

/**
 * Generate Gradle dependency snippet
 *
 * @param groupId - Maven group ID
 * @param artifactId - Maven artifact ID
 * @param version - Version
 * @returns Gradle dependency snippet
 */
export function generateGradleDependency(groupId: string, artifactId: string, version: string): string {
    return `implementation("${groupId}:${artifactId}:${version}")`;
}

/**
 * Generate pre-publish validation report
 *
 * @param options - Report options
 * @returns Markdown report content
 */
export function generatePrePublishReport(options: ReportOptions): string {
    const { version, groupId, artifacts, repositoryUrl } = options;
    const isPre = isPrerelease(version);
    const context = getGitHubContext();

    let report = `## 📋 Pre-Publish Validation Report\n\n`;

    // Version info
    report += `### Version Information\n\n`;
    report += generateMarkdownTable(
        ['Property', 'Value'],
        [
            ['Version', `\`${version}\``],
            ['Group ID', `\`${groupId}\``],
            ['Is Prerelease', isPre ? '⚠️ Yes' : '✅ No'],
            ['Artifact Count', `${artifacts.length}`],
        ]
    );
    report += '\n\n';

    // Build info
    report += `### Build Information\n\n`;
    report += generateMarkdownTable(
        ['Property', 'Value'],
        [
            ['Repository', repositoryUrl || context.repository || 'N/A'],
            ['Commit SHA', `\`${context.sha.substring(0, 7) || 'N/A'}\``],
            ['Branch/Tag', context.ref || 'N/A'],
            ['Workflow', context.workflow || 'N/A'],
            ['Run Number', `#${context.runNumber || 'N/A'}`],
            ['Triggered By', context.actor || 'N/A'],
        ]
    );
    report += '\n\n';

    // Artifacts to publish
    if (artifacts.length > 0) {
        report += `### Artifacts to Publish\n\n`;
        for (const artifact of artifacts) {
            report += `- \`${groupId}:${artifact}:${version}\`\n`;
        }
        report += '\n';
    }

    // Validation checklist
    report += `### Validation Checklist\n\n`;
    report += `- [x] Version format is valid\n`;
    report += `- [x] Build completed successfully\n`;
    report += `- [x] Tests passed\n`;
    report += isPre
        ? `- [ ] ⚠️ This is a prerelease version\n`
        : `- [x] This is a stable release\n`;
    report += '\n';

    // Warning for prerelease
    if (isPre) {
        report += `> ⚠️ **Warning**: This is a prerelease version (\`${version}\`). `;
        report += `It will be marked as prerelease on Maven Central.\n\n`;
    }

    return report;
}

/**
 * Generate publication summary report
 *
 * @param options - Report options
 * @returns Markdown report content
 */
export function generateSummaryReport(options: ReportOptions): string {
    const { version, groupId, artifacts, publishSuccess, repositoryUrl, releaseUrl } = options;
    const isPre = isPrerelease(version);
    const context = getGitHubContext();

    const statusIcon = publishSuccess ? '✅' : '❌';
    const statusText = publishSuccess ? 'Successful' : 'Failed';

    let report = `## ${statusIcon} Publication Summary\n\n`;

    // Status overview
    report += `### Status: ${statusText}\n\n`;
    report += generateMarkdownTable(
        ['Property', 'Value'],
        [
            ['Version', `\`${version}\``],
            ['Group ID', `\`${groupId}\``],
            ['Status', `**${statusText}**`],
            ['Is Prerelease', isPre ? 'Yes' : 'No'],
            ['Published At', new Date().toISOString()],
        ]
    );
    report += '\n\n';

    if (publishSuccess) {
        // Published artifacts
        if (artifacts.length > 0) {
            report += `### 📦 Published Artifacts\n\n`;
            const artifactRows = artifacts.map((artifact) => [
                `\`${artifact}\``,
                `[View](${getMavenSearchUrl(groupId, artifact)})`,
            ]);
            report += generateMarkdownTable(['Artifact', 'Maven Central'], artifactRows);
            report += '\n\n';
        }

        // Links section
        report += `### 🔗 Links\n\n`;
        const links: string[][] = [];

        if (releaseUrl) {
            links.push(['GitHub Release', `[View Release](${releaseUrl})`]);
        }
        if (repositoryUrl || context.repository) {
            const repoUrl = repositoryUrl || `https://github.com/${context.repository}`;
            links.push(['Repository', `[View Repository](${repoUrl})`]);
        }
        if (artifacts.length > 0) {
            const firstArtifact = artifacts[0]!;
            links.push(['Maven Central', `[Search](${getMavenSearchUrl(groupId, firstArtifact)})`]);
        }

        if (links.length > 0) {
            report += generateMarkdownTable(['Resource', 'Link'], links);
            report += '\n\n';
        }

        // Usage examples
        report += `### 📝 Usage Examples\n\n`;

        // Maven example
        const mavenExample = artifacts.length > 0
            ? generateMavenDependency(groupId, artifacts[0]!, version)
            : generateMavenDependency(groupId, 'your-artifact', version);

        report += generateCollapsible(
            'Maven',
            '```xml\n' + mavenExample + '\n```'
        );
        report += '\n\n';

        // Gradle example
        const gradleExample = artifacts.length > 0
            ? generateGradleDependency(groupId, artifacts[0]!, version)
            : generateGradleDependency(groupId, 'your-artifact', version);

        report += generateCollapsible(
            'Gradle (Kotlin DSL)',
            '```kotlin\n' + gradleExample + '\n```'
        );
        report += '\n\n';

        // Success message
        report += `> 🎉 **Congratulations!** Version \`${version}\` has been successfully published to Maven Central.\n`;
        report += `> It may take a few minutes for the artifacts to be available in search.\n\n`;
    }

    return report;
}

/**
 * Generate failure analysis report
 *
 * @param options - Report options
 * @returns Markdown report content
 */
export function generateFailureReport(options: ReportOptions): string {
    const { version, groupId, errorMessage, errorDetails, repositoryUrl } = options;
    const context = getGitHubContext();

    let report = `## ❌ Publication Failure Report\n\n`;

    // Error summary
    report += `### Error Summary\n\n`;
    report += generateMarkdownTable(
        ['Property', 'Value'],
        [
            ['Version', `\`${version}\``],
            ['Group ID', `\`${groupId}\``],
            ['Status', '**Failed**'],
            ['Failed At', new Date().toISOString()],
        ]
    );
    report += '\n\n';

    // Error message
    if (errorMessage) {
        report += `### Error Message\n\n`;
        report += `\`\`\`\n${errorMessage}\n\`\`\`\n\n`;
    }

    // Error details
    if (errorDetails) {
        report += generateCollapsible(
            '📋 Detailed Error Information',
            '```\n' + errorDetails + '\n```'
        );
        report += '\n\n';
    }

    // Build context
    report += `### Build Context\n\n`;
    report += generateMarkdownTable(
        ['Property', 'Value'],
        [
            ['Repository', repositoryUrl || context.repository || 'N/A'],
            ['Commit SHA', `\`${context.sha.substring(0, 7) || 'N/A'}\``],
            ['Branch/Tag', context.ref || 'N/A'],
            ['Workflow', context.workflow || 'N/A'],
            ['Run Number', `#${context.runNumber || 'N/A'}`],
            ['Triggered By', context.actor || 'N/A'],
        ]
    );
    report += '\n\n';

    // Troubleshooting suggestions
    report += `### 🔍 Troubleshooting Suggestions\n\n`;
    report += `1. **Check credentials**: Ensure Maven Central credentials are correctly configured\n`;
    report += `2. **Verify version**: Make sure the version doesn't already exist on Maven Central\n`;
    report += `3. **Review logs**: Check the full workflow logs for more details\n`;
    report += `4. **Network issues**: Retry if there were temporary network problems\n`;
    report += `5. **Signing**: Verify GPG signing is properly configured\n\n`;

    // Common errors
    report += generateCollapsible(
        '📚 Common Error Patterns',
        `
| Error | Possible Cause | Solution |
| --- | --- | --- |
| 401 Unauthorized | Invalid credentials | Check OSSRH_USERNAME and OSSRH_PASSWORD secrets |
| 403 Forbidden | Insufficient permissions | Verify account has publish rights |
| 409 Conflict | Version already exists | Use a new version number |
| Connection timeout | Network issues | Retry the workflow |
| GPG signing failed | Missing or invalid key | Check GPG_PRIVATE_KEY and GPG_PASSPHRASE secrets |
`
    );
    report += '\n\n';

    // Retry instructions
    report += `### 🔄 Retry Instructions\n\n`;
    report += `To retry the publication:\n\n`;
    report += `1. Fix the underlying issue\n`;
    report += `2. Re-run the workflow from the Actions tab\n`;
    report += `3. Or trigger a new release with the same version (if version doesn't exist)\n\n`;

    return report;
}

/**
 * Generate report based on type
 *
 * @param options - Report options
 * @returns Markdown report content
 */
export function generateReport(options: ReportOptions): string {
    switch (options.reportType) {
        case 'pre-publish':
            return generatePrePublishReport(options);
        case 'summary':
            return generateSummaryReport(options);
        case 'failure':
            return generateFailureReport(options);
        default:
            throw new Error(`Unknown report type: ${options.reportType}`);
    }
}

/**
 * Validate report type input
 *
 * @param input - Input string
 * @returns Valid report type
 */
function validateReportType(input: string): ReportType {
    const validTypes: ReportType[] = ['pre-publish', 'summary', 'failure'];
    if (!validTypes.includes(input as ReportType)) {
        throw new Error(`Invalid report type: ${input}. Must be one of: ${validTypes.join(', ')}`);
    }
    return input as ReportType;
}

/**
 * Main entry point for the action
 */
async function run(): Promise<void> {
    try {
        // Parse inputs
        const reportTypeInput = getInput('report-type', true);
        const reportType = validateReportType(reportTypeInput);
        const version = getInput('version', true);
        const groupId = getInput('group-id') || 'io.github.truenine';
        const artifactsInput = getInput('artifacts') || '';
        const artifacts = artifactsInput
            .split(',')
            .map((a) => a.trim())
            .filter((a) => a.length > 0);
        const publishSuccess = getBooleanInput('publish-success');
        const errorMessage = getInput('error-message') || '';
        const errorDetails = getInput('error-details') || '';
        const repositoryUrl = getInput('repository-url') || '';
        const releaseUrl = getInput('release-url') || '';

        info(`Report Type: ${reportType}`);
        info(`Version: ${version}`);
        info(`Group ID: ${groupId}`);
        info(`Artifacts: ${artifacts.length > 0 ? artifacts.join(', ') : 'none'}`);

        const options: ReportOptions = {
            reportType,
            version,
            groupId,
            artifacts,
            publishSuccess,
            errorMessage,
            errorDetails,
            repositoryUrl,
            releaseUrl,
        };

        // Generate report
        const report = generateReport(options);

        // Set outputs
        setOutput('report', report.substring(0, 1000)); // Truncate for output
        setOutput('report-type', reportType);

        // Write to step summary
        await writeStepSummary(report);

        info(`✅ Generated ${reportType} report`);
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
