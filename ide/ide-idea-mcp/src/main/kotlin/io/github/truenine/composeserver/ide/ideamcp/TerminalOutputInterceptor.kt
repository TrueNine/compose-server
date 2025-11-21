package io.github.truenine.composeserver.ide.ideamcp

import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.execution.process.OSProcessHandler
import com.intellij.execution.process.ProcessEvent
import com.intellij.execution.process.ProcessListener
import com.intellij.openapi.Disposable
import com.intellij.openapi.components.Service
import com.intellij.openapi.util.Key
import java.nio.charset.Charset
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory

/**
 * Terminal output interceptor.
 *
 * Responsible for capturing and processing the output of terminal commands.
 */
@Service(Service.Level.PROJECT)
class TerminalOutputInterceptor(private val scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)) : Disposable {

  private val logger = LoggerFactory.getLogger(TerminalOutputInterceptor::class.java)

  /** Result of executing a command with captured output. */
  data class CommandResult(val command: String, val exitCode: Int, val stdout: String, val stderr: String, val cleanedOutput: String)

  /** Execute a command and capture its output. */
  fun executeCommand(command: String, workingDirectory: String? = null, onResult: (CommandResult) -> Unit) {
    scope.launch {
      try {
        logger.info("Executing terminal command: {}", command)

        // Parse command-line arguments
        val parts = command.split(" ").filter { it.isNotEmpty() }
        if (parts.isEmpty()) {
          throw IllegalArgumentException("Empty command")
        }

        val commandLine =
          GeneralCommandLine().apply {
            exePath = parts[0]
            if (parts.size > 1) {
              addParameters(parts.drop(1))
            }
            workingDirectory?.let { withWorkDirectory(it) }
            charset = Charset.forName("UTF-8")
          }

        val processHandler = OSProcessHandler(commandLine)
        val stdout = StringBuilder()
        val stderr = StringBuilder()

        processHandler.addProcessListener(
          object : ProcessListener {
            override fun processTerminated(event: ProcessEvent) {
              val stdoutText = stdout.toString()
              val stderrText = stderr.toString()

              // Log raw output
              if (stdoutText.isNotEmpty()) {
                logger.debug("Terminal stdout: {}", stdoutText)
              }
              if (stderrText.isNotEmpty()) {
                logger.warn("Terminal stderr: {}", stderrText)
              }

              // Clean output
              val cleanedOutput = cleanOutput(stdoutText, stderrText)
              logger.info("Output cleaning completed - original length: {}, cleaned length: {}", stdoutText.length + stderrText.length, cleanedOutput.length)

              val result = CommandResult(command = command, exitCode = event.exitCode, stdout = stdoutText, stderr = stderrText, cleanedOutput = cleanedOutput)

              onResult(result)
            }

            override fun onTextAvailable(event: ProcessEvent, outputType: Key<*>) {
              val text = event.text
              if (outputType.toString().contains("STDOUT")) {
                stdout.append(text)
              } else if (outputType.toString().contains("STDERR")) {
                stderr.append(text)
              }
            }
          }
        )

        processHandler.startNotify()
      } catch (e: Exception) {
        logger.error("Command execution failed: {}", command, e)
        val errorResult =
          CommandResult(
            command = command,
            exitCode = -1,
            stdout = "",
            stderr = e.message ?: "Unknown error",
            cleanedOutput = "Command execution failed: ${e.message}",
          )
        onResult(errorResult)
      }
    }
  }

  /** Clean output by removing unnecessary control characters and formatting. */
  private fun cleanOutput(stdout: String, stderr: String): String {
    val combinedOutput =
      if (stderr.isNotEmpty()) {
        "Standard output:\n$stdout\n\nError output:\n$stderr"
      } else {
        stdout
      }

    return combinedOutput
      .lines()
      .filter { line -> line.trim().isNotEmpty() } // Remove empty lines
      .joinToString("\n") { line ->
        line
          .replace(Regex("\u001B\\[[;\\d]*m"), "") // Remove ANSI color codes
          .replace(Regex("\\r"), "") // Remove carriage returns
          .trim()
      }
      .let { cleaned ->
        // Limit output length to avoid overly long logs
        if (cleaned.length > 5000) {
          cleaned.take(5000) + "\n...(output truncated)"
        } else {
          cleaned
        }
      }
  }

  /** Advanced cleaning, can be extended with AI post-processing. */
  fun enhancedCleanOutput(output: String): String {
    // TODO: integrate AI service for smarter cleaning
    // Currently uses basic text-processing rules
    return output
      .lines()
      .filter { line ->
        val trimmed = line.trim()
        // Filter common noisy output
        trimmed.isNotEmpty() &&
          !trimmed.startsWith("[INFO]") &&
          !trimmed.startsWith("[DEBUG]") &&
          !trimmed.contains("BUILD SUCCESSFUL") &&
          !trimmed.contains("seconds")
      }
      .joinToString("\n")
  }

  override fun dispose() {
    // Cancel all coroutines
    scope.cancel()
    logger.debug("TerminalOutputInterceptor disposed")
  }
}
