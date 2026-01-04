/**
 * Unit tests for Extract Versions Action
 *
 * Tests version extraction functionality and error handling.
 *
 * **Validates: Requirements 2.2, 2.3, 2.4, 2.5, 2.6**
 */

import { mkdir, rm, writeFile } from 'node:fs/promises'
import { tmpdir } from 'node:os'
import { join } from 'node:path'
import { extractVersions, extractVersionsFromToml, parseToml, TomlParseError } from '@github-actions/shared'
import { afterEach, beforeEach, describe, expect, it } from 'vitest'

describe('extract Versions Action', () => {
  let testDir: string

  beforeEach(async () => {
    // Create a temporary directory for test files
    testDir = join(tmpdir(), `extract-versions-test-${Date.now()}`)
    await mkdir(testDir, { recursive: true })
  })

  afterEach(async () => {
    // Clean up temporary directory
    await rm(testDir, { recursive: true, force: true })
  })

  describe('版本提取功能', () => {
    it('应该从有效的 TOML 文件中提取版本信息', async () => {
      const tomlContent = `[versions]
java = "24"
org-gradle = "9.0"
project = "0.3.0"
`
      const tomlPath = join(testDir, 'libs.versions.toml')
      await writeFile(tomlPath, tomlContent)

      const versions = await extractVersions(tomlPath)

      expect(versions.java).toBe('24')
      expect(versions.gradle).toBe('9.0')
      expect(versions.project).toBe('0.3.0')
    })

    it('应该从包含其他内容的 TOML 文件中提取版本', async () => {
      const tomlContent = `[versions]
java = "21"
org-gradle = "8.5"
project = "1.0.0-SNAPSHOT"
kotlin = "2.0.0"

[libraries]
some-lib = { module = "com.example:lib", version = "1.0.0" }

[plugins]
kotlin-jvm = { id = "org.jetbrains.kotlin.jvm", version.ref = "kotlin" }
`
      const tomlPath = join(testDir, 'libs.versions.toml')
      await writeFile(tomlPath, tomlContent)

      const versions = await extractVersions(tomlPath)

      expect(versions.java).toBe('21')
      expect(versions.gradle).toBe('8.5')
      expect(versions.project).toBe('1.0.0-SNAPSHOT')
    })

    it('应该正确处理带有预发布标识的版本', async () => {
      const tomlContent = `[versions]
java = "24"
org-gradle = "9.0-rc-1"
project = "0.3.0-alpha.1"
`
      const tomlPath = join(testDir, 'libs.versions.toml')
      await writeFile(tomlPath, tomlContent)

      const versions = await extractVersions(tomlPath)

      expect(versions.java).toBe('24')
      expect(versions.gradle).toBe('9.0-rc-1')
      expect(versions.project).toBe('0.3.0-alpha.1')
    })
  })

  describe('错误处理', () => {
    it('应该在文件不存在时抛出 TomlParseError', async () => {
      const nonExistentPath = join(testDir, 'non-existent.toml')

      await expect(extractVersions(nonExistentPath)).rejects.toThrow(TomlParseError)
    })

    it('应该在 TOML 格式错误时抛出 TomlParseError', async () => {
      const invalidToml = `[versions
java = "24"
`
      const tomlPath = join(testDir, 'invalid.toml')
      await writeFile(tomlPath, invalidToml)

      await expect(extractVersions(tomlPath)).rejects.toThrow(TomlParseError)
    })

    it('应该在缺少 versions 节时抛出 TomlParseError', async () => {
      const tomlContent = `[libraries]
some-lib = "1.0.0"
`
      const tomlPath = join(testDir, 'no-versions.toml')
      await writeFile(tomlPath, tomlContent)

      await expect(extractVersions(tomlPath)).rejects.toThrow(TomlParseError)
    })

    it('应该在缺少 java 版本时抛出 TomlParseError', async () => {
      const tomlContent = `[versions]
org-gradle = "9.0"
project = "0.3.0"
`
      const tomlPath = join(testDir, 'missing-java.toml')
      await writeFile(tomlPath, tomlContent)

      await expect(extractVersions(tomlPath)).rejects.toThrow(TomlParseError)
      await expect(extractVersions(tomlPath)).rejects.toThrow(/java/)
    })

    it('应该在缺少 org-gradle 版本时抛出 TomlParseError', async () => {
      const tomlContent = `[versions]
java = "24"
project = "0.3.0"
`
      const tomlPath = join(testDir, 'missing-gradle.toml')
      await writeFile(tomlPath, tomlContent)

      await expect(extractVersions(tomlPath)).rejects.toThrow(TomlParseError)
      await expect(extractVersions(tomlPath)).rejects.toThrow(/org-gradle/)
    })

    it('应该在缺少 project 版本时抛出 TomlParseError', async () => {
      const tomlContent = `[versions]
java = "24"
org-gradle = "9.0"
`
      const tomlPath = join(testDir, 'missing-project.toml')
      await writeFile(tomlPath, tomlContent)

      await expect(extractVersions(tomlPath)).rejects.toThrow(TomlParseError)
      await expect(extractVersions(tomlPath)).rejects.toThrow(/project/)
    })

    it('应该在缺少多个版本字段时列出所有缺失字段', async () => {
      const tomlContent = `[versions]
other = "1.0.0"
`
      const tomlPath = join(testDir, 'missing-all.toml')
      await writeFile(tomlPath, tomlContent)

      await expect(extractVersions(tomlPath)).rejects.toThrow(TomlParseError)
      await expect(extractVersions(tomlPath)).rejects.toThrow(/java/)
      await expect(extractVersions(tomlPath)).rejects.toThrow(/org-gradle/)
      await expect(extractVersions(tomlPath)).rejects.toThrow(/project/)
    })

    it('应该在空文件时抛出 TomlParseError', async () => {
      const tomlPath = join(testDir, 'empty.toml')
      await writeFile(tomlPath, '')

      await expect(extractVersions(tomlPath)).rejects.toThrow(TomlParseError)
    })
  })

  describe('parseToml 函数', () => {
    it('应该正确解析有效的 TOML 内容', () => {
      const tomlContent = `[versions]
java = "24"
`
      const result = parseToml(tomlContent)

      expect(result).toHaveProperty('versions')
      expect((result.versions as Record<string, string>).java).toBe('24')
    })

    it('应该在空内容时抛出错误', () => {
      expect(() => parseToml('')).toThrow(TomlParseError)
      expect(() => parseToml('   ')).toThrow(TomlParseError)
      expect(() => parseToml('\n\t')).toThrow(TomlParseError)
    })
  })

  describe('extractVersionsFromToml 函数', () => {
    it('应该从解析后的 TOML 对象中提取版本', () => {
      const toml = {
        versions: {
          'java': '24',
          'org-gradle': '9.0',
          'project': '0.3.0',
        },
      }

      const versions = extractVersionsFromToml(toml)

      expect(versions.java).toBe('24')
      expect(versions.gradle).toBe('9.0')
      expect(versions.project).toBe('0.3.0')
    })

    it('应该将非字符串版本值转换为字符串', () => {
      const toml = {
        versions: {
          'java': 24,
          'org-gradle': 9.0,
          'project': '0.3.0',
        },
      }

      const versions = extractVersionsFromToml(toml as unknown as Record<string, unknown>)

      expect(versions.java).toBe('24')
      expect(versions.gradle).toBe('9')
      expect(versions.project).toBe('0.3.0')
    })
  })
})
