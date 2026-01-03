/**
 * Unit tests for Maven Version Check Action
 *
 * Tests HTTP request logic and retry mechanism.
 *
 * **Validates: Requirements 4.2, 4.3, 4.4, 4.5, 4.6**
 */

import { describe, it, expect, vi, beforeEach, afterEach } from 'vitest';
import { buildMavenCentralUrl, checkArtifactExists, performVersionCheck } from './index.js';

// Mock fetch globally
const mockFetch = vi.fn();
vi.stubGlobal('fetch', mockFetch);

describe('Maven Version Check Action', () => {
    beforeEach(() => {
        vi.clearAllMocks();
    });

    afterEach(() => {
        vi.restoreAllMocks();
    });

    describe('buildMavenCentralUrl', () => {
        it('应该正确构建 Maven Central URL', () => {
            const url = buildMavenCentralUrl('io.github.truenine', 'composeserver-shared', '0.3.0');

            expect(url).toBe(
                'https://repo1.maven.org/maven2/io/github/truenine/composeserver-shared/0.3.0/composeserver-shared-0.3.0.pom'
            );
        });

        it('应该正确处理多级 groupId', () => {
            const url = buildMavenCentralUrl('com.example.deep.nested', 'my-artifact', '1.0.0');

            expect(url).toBe(
                'https://repo1.maven.org/maven2/com/example/deep/nested/my-artifact/1.0.0/my-artifact-1.0.0.pom'
            );
        });

        it('应该正确处理带预发布标识的版本', () => {
            const url = buildMavenCentralUrl('io.github.truenine', 'composeserver-bom', '0.3.0-alpha.1');

            expect(url).toBe(
                'https://repo1.maven.org/maven2/io/github/truenine/composeserver-bom/0.3.0-alpha.1/composeserver-bom-0.3.0-alpha.1.pom'
            );
        });
    });

    describe('checkArtifactExists', () => {
        it('应该在 artifact 存在时返回 true', async () => {
            mockFetch.mockResolvedValueOnce({
                status: 200,
            });

            const exists = await checkArtifactExists('io.github.truenine', 'composeserver-shared', '0.3.0');

            expect(exists).toBe(true);
            expect(mockFetch).toHaveBeenCalledTimes(1);
        });

        it('应该在 artifact 不存在时返回 false (404)', async () => {
            mockFetch.mockResolvedValueOnce({
                status: 404,
            });

            const exists = await checkArtifactExists('io.github.truenine', 'composeserver-shared', '0.3.0');

            expect(exists).toBe(false);
            expect(mockFetch).toHaveBeenCalledTimes(1);
        });

        it('应该在服务器错误时进行重试', async () => {
            mockFetch
                .mockRejectedValueOnce(new Error('Network error'))
                .mockRejectedValueOnce(new Error('Network error'))
                .mockResolvedValueOnce({ status: 200 });

            const exists = await checkArtifactExists('io.github.truenine', 'composeserver-shared', '0.3.0', 3);

            expect(exists).toBe(true);
            expect(mockFetch).toHaveBeenCalledTimes(3);
        });

        it('应该在所有重试失败后抛出 MavenCentralError', async () => {
            mockFetch.mockRejectedValue(new Error('Network error'));

            await expect(
                checkArtifactExists('io.github.truenine', 'composeserver-shared', '0.3.0', 3)
            ).rejects.toThrow('Maven Central Error');

            expect(mockFetch).toHaveBeenCalledTimes(3);
        });

        it('应该在客户端错误时不重试 (4xx except 404)', async () => {
            mockFetch.mockResolvedValueOnce({
                status: 403,
            });

            const exists = await checkArtifactExists('io.github.truenine', 'composeserver-shared', '0.3.0');

            expect(exists).toBe(false);
            expect(mockFetch).toHaveBeenCalledTimes(1);
        });

        it('应该使用正确的 HTTP 方法和 headers', async () => {
            mockFetch.mockResolvedValueOnce({ status: 200 });

            await checkArtifactExists('io.github.truenine', 'composeserver-shared', '0.3.0');

            expect(mockFetch).toHaveBeenCalledWith(
                expect.any(String),
                expect.objectContaining({
                    method: 'HEAD',
                    headers: expect.objectContaining({
                        'User-Agent': 'GitHub-Actions-Maven-Version-Check/1.0',
                    }),
                })
            );
        });
    });

    describe('performVersionCheck', () => {
        it('应该在 force publish 时跳过检查', async () => {
            const result = await performVersionCheck('0.3.0', 'io.github.truenine', ['composeserver-shared'], true);

            expect(result.shouldPublish).toBe(true);
            expect(result.versionExistsOnCentral).toBe(false);
            expect(mockFetch).not.toHaveBeenCalled();
        });

        it('应该在 snapshot 版本时跳过检查', async () => {
            const result = await performVersionCheck(
                '0.3.0-SNAPSHOT',
                'io.github.truenine',
                ['composeserver-shared'],
                false
            );

            expect(result.shouldPublish).toBe(true);
            expect(result.isSnapshot).toBe(true);
            expect(result.versionExistsOnCentral).toBe(false);
            expect(mockFetch).not.toHaveBeenCalled();
        });

        it('应该在 prerelease 版本时跳过检查', async () => {
            const result = await performVersionCheck(
                '0.3.0-alpha.1',
                'io.github.truenine',
                ['composeserver-shared'],
                false
            );

            expect(result.shouldPublish).toBe(true);
            expect(result.isSnapshot).toBe(true);
            expect(mockFetch).not.toHaveBeenCalled();
        });

        it('应该在版本存在时返回 shouldPublish=false', async () => {
            mockFetch.mockResolvedValue({ status: 200 });

            const result = await performVersionCheck(
                '0.3.0',
                'io.github.truenine',
                ['composeserver-shared', 'composeserver-bom'],
                false
            );

            expect(result.shouldPublish).toBe(false);
            expect(result.versionExistsOnCentral).toBe(true);
        });

        it('应该在版本不存在时返回 shouldPublish=true', async () => {
            mockFetch.mockResolvedValue({ status: 404 });

            const result = await performVersionCheck(
                '0.3.0',
                'io.github.truenine',
                ['composeserver-shared', 'composeserver-bom'],
                false
            );

            expect(result.shouldPublish).toBe(true);
            expect(result.versionExistsOnCentral).toBe(false);
        });

        it('应该在任一 artifact 存在时返回 versionExistsOnCentral=true', async () => {
            mockFetch
                .mockResolvedValueOnce({ status: 404 }) // first artifact not found
                .mockResolvedValueOnce({ status: 200 }); // second artifact exists

            const result = await performVersionCheck(
                '0.3.0',
                'io.github.truenine',
                ['composeserver-shared', 'composeserver-bom'],
                false
            );

            expect(result.versionExistsOnCentral).toBe(true);
            expect(result.shouldPublish).toBe(false);
        });

        it('应该正确处理带 v 前缀的版本', async () => {
            mockFetch.mockResolvedValue({ status: 404 });

            const result = await performVersionCheck(
                'v0.3.0',
                'io.github.truenine',
                ['composeserver-shared'],
                false
            );

            expect(result.version).toBe('0.3.0');
            expect(result.shouldPublish).toBe(true);
        });

        it('应该正确识别 rc 版本为 prerelease', async () => {
            const result = await performVersionCheck(
                '0.3.0-rc.1',
                'io.github.truenine',
                ['composeserver-shared'],
                false
            );

            expect(result.isSnapshot).toBe(true);
            expect(result.shouldPublish).toBe(true);
        });

        it('应该正确识别 beta 版本为 prerelease', async () => {
            const result = await performVersionCheck(
                '0.3.0-beta',
                'io.github.truenine',
                ['composeserver-shared'],
                false
            );

            expect(result.isSnapshot).toBe(true);
            expect(result.shouldPublish).toBe(true);
        });
    });
});
