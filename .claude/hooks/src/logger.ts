import {appendFile} from "fs/promises";
import {spawn} from "child_process";
import process from 'process';

/**
 * 播放提示音 - Windows 兼容版本
 */
function playSound() {
  try {
    if (process.platform === 'win32') {
      // Windows 系统使用 PowerShell 播放系统声音
      spawn('powershell', ['-Command', '[System.Console]::Beep(800, 200)'], {
        stdio: 'ignore',
        windowsHide: true
      });
    } else {
      // Linux/macOS 系统
      try {
        spawn('speaker-test', ['-t', 'sine', '-f', '800', '-l', '1', '-s', '1'], {
          stdio: 'ignore',
          timeout: 1000
        });
      } catch {
        try {
          spawn('pactl', ['play-sample', '0'], {
            stdio: 'ignore',
            timeout: 1000
          });
        } catch {
          // 最后回退到控制台响铃
          process.stdout.write('\x07');
        }
      }
    }
  } catch (error) {
    // 静默处理错误，使用控制台响铃作为最后手段
    process.stdout.write('\x07');
  }
}


export function logger(
  debugLogPath: string
) {
  const _now = () => {
    return Date.now().toString()
  }
  const _title = (level: string) => {
    return `[${_now()}] ${level} - `
  }

  const sound = () => {
    playSound()
  }
  const sounderr = () => {
    playSound()
  }
  type MessageType = string | null | undefined
  const toFile = async (msg: MessageType) => {
    try {
      await appendFile(
        debugLogPath,
        (msg || '') + '\n',
        {
          flush: true
        }
      )
    } catch (error) {
      console.error('Failed to write to log file:', error)
    }
  }

  const error = async (msg: MessageType) => {
    const _msg = _title('ERROR') + (msg || '')
    err(msg)
    sounderr()
    await toFile(_msg)
  }
  const warn = async (msg: MessageType) => {
    const _msg = _title('WARN') + (msg || '')
    await toFile(_msg)
  }
  const info = async (msg: MessageType) => {
    const _msg = _title('INFO') + (msg || '')
    await toFile(_msg)
  }
  const debug = async (msg: MessageType) => {
    const _msg = _title('DEBUG') + (msg || '')
    await toFile(_msg)
  }
  const trace = async (msg: MessageType) => {
    const _msg = _title('TRACE') + (msg || '')
    await toFile(_msg)
  }
  const out = async (msg: MessageType) => {
    const _msg = _title('STDOUT') + (msg || '')
    await toFile(_msg)
    if (msg) {
      process.stdout.write(msg)
    }
  }

  const err = async (msg: MessageType) => {
    const _msg = _title('STDERR') + (msg || '')
    await toFile(_msg)
    if (msg) {
      process.stderr.write(msg)
    }
  }

  const hookerr = async (msg: MessageType) => {
    const _msg = _title('HOOK_ERROR') + (msg || '')
    await toFile(_msg)
    if (msg) {
      process.stderr.write(msg)
    }
  }

  return {
    err, error,
    warn, info,
    debug, trace,
    out, sound,
    sounderr, hookerr
  }
}
