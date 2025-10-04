#!/usr/bin/env node

import {exec} from 'child_process';
import {promisify} from 'util';
import {existsSync} from 'fs';
import {dirname, join, resolve} from 'path';
import {fileURLToPath} from 'url';
import {readStdin} from './src/claude'
import {logger} from './src/logger'

const execAsync = promisify(exec);
const __filename = fileURLToPath(import.meta.url);
const __dirname = dirname(__filename);


// 获取项目根目录
const projectRoot = resolve(__dirname, '../..');
const debugLogPath = join(projectRoot, '.claude', 'hook-debug.log');

const log = logger(debugLogPath)

/**
 * 执行命令并返回结果
 * @param {string} command - 要执行的命令
 * @param {string} cwd - 工作目录
 * @returns {Promise<{success: boolean, output: string, error?: string}>}
 */
async function runCommand(command: string, cwd: string) {
  try {
    log.debug(`Executing command: ${command}`);
    log.debug(`Working directory: ${cwd}`);

    const {stdout, stderr} = await execAsync(command, {
      cwd,
      timeout: 120000, // 增加到 2 分钟
      windowsHide: true,
      shell: true // 确保在 Windows 下使用 shell
    });

    const output = stdout + stderr;
    log.debug(`Command output: ${output}`);

    return {success: true, output};
  } catch (error: any) {
    const errorMessage = error.message || 'Unknown error';
    const stdout = error.stdout || '';
    const stderr = error.stderr || '';
    const output = stdout + stderr;
    const exitCode = error.code || 'unknown';
    const signal = error.signal || 'none';

    log.error(`Command failed: ${command}`);
    log.error(`Working directory: ${cwd}`);
    log.error(`Exit code: ${exitCode}`);
    log.error(`Signal: ${signal}`);
    log.error(`Error message: ${errorMessage}`);
    log.error(`Stdout: ${stdout}`);
    log.error(`Stderr: ${stderr}`);
    log.error(`Combined output: ${output}`);

    return {
      success: false,
      output,
      error: errorMessage
    };
  }
}

/**
 * 检查前端文件
 * @param {string} filePath - 文件路径
 */
async function checkFrontendFile(filePath: string) {
  log.info(`Starting frontend file check for: ${filePath}`);

  const frontendDir = join(projectRoot, 'frontend');

  if (!existsSync(frontendDir)) {
    log.warn(`Frontend directory does not exist: ${frontendDir}`);
    return;
  }

  // 支持 Windows 和 Unix 路径格式
  if (!filePath.includes('frontend/') && !filePath.includes('frontend\\')) {
    log.debug(`File path does not contain frontend directory: ${filePath}`);
    return;
  }

  const relativePath = filePath.includes('frontend/')
    ? filePath.split('frontend/')[1]
    : filePath.split('frontend\\')[1];
  const fullPath = join(frontendDir, relativePath);

  log.info(`Processing file: ${relativePath}`);
  log.debug(`Full path: ${fullPath}`);

  if (!existsSync(fullPath)) {
    log.warn(`File does not exist: ${fullPath}`);
    return;
  }

  const errors = [];
  const warnings = [];

  // 并行运行所有检查命令
  const commands = [
    runCommand(
      `pnpm prettier --write "${relativePath}" --log-level error`,
      frontendDir
    ),
    runCommand(
      `pnpm eslint --fix "${relativePath}"`,
      frontendDir
    )
  ];

  // 对 TypeScript 文件添加类型检查
  if (relativePath.endsWith('.ts') || relativePath.endsWith('.tsx')) {
    log.debug(`Adding TypeScript check for: ${relativePath}`);
    commands.push(
      runCommand(
        'pnpm tsc --noEmit --skipLibCheck',
        frontendDir
      )
    );
  }

  log.info(`Running ${commands.length} commands for file: ${relativePath}`);

  // 等待所有命令完成
  const [prettierResult, eslintResult, tscResult] = await Promise.all(commands);

  // 处理 Prettier 结果
  if (!prettierResult.success) {
    if (prettierResult.output.trim()) {
      errors.push(`Prettier failed: ${prettierResult.output}`);
    }
    if (prettierResult.error) {
      errors.push(`Prettier error: ${prettierResult.error}`);
    }
  } else if (prettierResult.output.trim()) {
    log.debug(`Prettier output: ${prettierResult.output}`);
  }

  // 处理 ESLint 结果
  if (!eslintResult.success) {
    if (eslintResult.output.trim()) {
      const lines = eslintResult.output.trim().split('\n');
      if (lines.length > 0) {
        errors.push(...lines);
      }
    }
    if (eslintResult.error) {
      errors.push(`ESLint error: ${eslintResult.error}`);
    }
  } else if (eslintResult.output.trim()) {
    // ESLint warnings/info (non-error output when success is true)
    const lines = eslintResult.output.trim().split('\n');
    if (lines.length > 0 && lines[0] !== '') {
      warnings.push(...lines);
    }
  }

  // 处理 TypeScript 结果（如果存在）
  if (tscResult) {
    if (!tscResult.success) {
      if (tscResult.output.trim()) {
        const lines = tscResult.output.trim().split('\n');
        if (lines.length > 0) {
          errors.push(...lines);
        }
      }
      if (tscResult.error) {
        errors.push(`TypeScript error: ${tscResult.error}`);
      }
    } else if (tscResult.output.trim()) {
      log.debug(`TypeScript output: ${tscResult.output}`);
    }
  }

  // 输出结果
  if (errors.length > 0) {
    log.error(`Found ${errors.length} error(s) in ${relativePath}`);
    log.error(errors.join('\n'));
  } else {
    if (warnings.length > 0) {
      log.warn(`Found ${warnings.length} warning(s) in ${relativePath}`);
      log.warn(warnings.join('\n'));
    }
    log.info(`✅ File check completed successfully: ${relativePath}`);
    log.sound();
  }
}

/**
 * 主函数
 */
async function main() {
  try {
    log.info(`🔧 Claude Code Hook Started`);
    log.debug(`Process ID: ${process.pid}`);
    log.debug(`Working directory: ${process.cwd()}`);
    log.debug(`Platform: ${process.platform}`);

    // 检查是否有标准输入
    if (process.stdin.isTTY) {
      log.debug(`No stdin input detected, exiting...`);
      return;
    }

    // 读取标准输入
    let stdinString = '';
    for await (const chunk of process.stdin) {
      stdinString += chunk;
    }

    log.info(`📥 Hook triggered with ${stdinString.length} characters of input`);
    log.debug(`Raw input: ${stdinString}`);

    const data = readStdin(stdinString);
    log.debug(`Parsed data: ${JSON.stringify(data, null, 2)}`);

    if (!data) {
      log.warn(`Failed to parse stdin data`);
      return;
    }

    const filePath = (data?.tool_input?.file_path || '').replace(/\\/g, '/');
    const toolName = data?.tool_name || 'unknown';
    const hookEvent = data?.hook_event_name || 'unknown';

    log.info(`📄 Tool: ${toolName}, Event: ${hookEvent}`);
    log.info(`📁 File path: ${filePath}`);

    if (!filePath) {
      log.debug(`No file_path found in tool_input, skipping processing`);
      return;
    }

    const isFrontendFile = filePath.includes('frontend/') || filePath.includes('frontend\\');
    log.debug(`Is frontend file: ${isFrontendFile}`);

    if (isFrontendFile) {
      log.info(`🎯 Processing frontend file...`);
      log.sound();
      await checkFrontendFile(filePath);
      log.info(`✨ Hook processing completed`);
    } else {
      log.debug(`File is not in frontend directory, skipping processing`);
    }
  } catch (error) {
    if (error instanceof Error) {
      log.error(`❌ Unexpected error in main function: ${error.message}`);
      log.error(`Stack trace: ${error.stack}`);
    } else {
      log.error(`❌ Unknown error occurred: ${error}`);
    }
  }
}

// 运行主函数
main().catch(error => {
  log.error(`Unhandled error: ${error?.message}`);
  log.error(`❌ Hook execution failed: ${error?.message}`);
  process.exit(1);
});
