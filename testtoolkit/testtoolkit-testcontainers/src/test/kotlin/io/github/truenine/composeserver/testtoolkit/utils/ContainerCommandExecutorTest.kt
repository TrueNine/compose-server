package io.github.truenine.composeserver.testtoolkit.utils

import io.github.truenine.composeserver.testtoolkit.log
import java.time.Duration
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import org.testcontainers.containers.GenericContainer
import org.testcontainers.utility.DockerImageName

/**
 * # 容器命令执行器测试
 *
 * 测试 ContainerCommandExecutor 类的所有功能，确保100%覆盖率
 *
 * @author TrueNine
 * @since 2025-07-12
 */
class ContainerCommandExecutorTest {

  @Test
  fun `测试 ContainerCommandExecutor 构造函数和常量`() {
    log.info("开始测试 ContainerCommandExecutor 构造函数和常量")

    GenericContainer(DockerImageName.parse("alpine:3.21")).withCommand("sleep", "infinity").use { container ->
      container.start()

      val executor = ContainerCommandExecutor(container)
      assertNotNull(executor, "命令执行器不应该为 null")

      // 测试常量值
      assertEquals(Duration.ofSeconds(30), ContainerCommandExecutor.DEFAULT_COMMAND_TIMEOUT, "默认超时时间应该是30秒")
      assertEquals(3, ContainerCommandExecutor.DEFAULT_MAX_RETRIES, "默认重试次数应该是3")

      log.info("ContainerCommandExecutor 构造函数和常量测试完成")
    }
  }

  @Test
  fun `测试 executeCommand 方法的成功执行`() {
    log.info("开始测试 executeCommand 方法的成功执行")

    GenericContainer(DockerImageName.parse("alpine:3.21")).withCommand("sleep", "infinity").use { container ->
      container.start()

      val executor = ContainerCommandExecutor(container)
      val result = executor.executeCommand(commands = arrayOf("echo", "Hello World"))

      assertEquals(0, result.exitCode, "命令执行应该成功")
      assertTrue(result.stdout.contains("Hello World"), "输出应该包含预期内容")

      log.info("executeCommand 方法成功执行测试完成")
    }
  }

  @Test
  fun `测试 executeCommand 方法的空命令验证`() {
    log.info("开始测试 executeCommand 方法的空命令验证")

    GenericContainer(DockerImageName.parse("alpine:3.21")).withCommand("sleep", "infinity").use { container ->
      container.start()

      val executor = ContainerCommandExecutor(container)

      assertFailsWith<IllegalArgumentException> { executor.executeCommand() }

      log.info("executeCommand 方法空命令验证测试完成")
    }
  }

  @Test
  fun `测试 executeCommand 方法的自定义参数`() {
    log.info("开始测试 executeCommand 方法的自定义参数")

    GenericContainer(DockerImageName.parse("alpine:3.21")).withCommand("sleep", "infinity").use { container ->
      container.start()

      val executor = ContainerCommandExecutor(container)
      val result = executor.executeCommand(timeout = Duration.ofSeconds(10), maxRetries = 2, "echo", "Custom Test")

      assertEquals(0, result.exitCode, "自定义参数的命令执行应该成功")
      assertTrue(result.stdout.contains("Custom Test"), "输出应该包含预期内容")

      log.info("executeCommand 方法自定义参数测试完成")
    }
  }

  @Test
  fun `测试 executeCommandWithExpectedExitCode 方法的成功情况`() {
    log.info("开始测试 executeCommandWithExpectedExitCode 方法的成功情况")

    GenericContainer(DockerImageName.parse("alpine:3.21")).withCommand("sleep", "infinity").use { container ->
      container.start()

      val executor = ContainerCommandExecutor(container)
      val result = executor.executeCommandWithExpectedExitCode(0, commands = arrayOf("echo", "Success"))

      assertEquals(0, result.exitCode, "期望退出码为0的命令应该成功")
      assertTrue(result.stdout.contains("Success"), "输出应该包含预期内容")

      log.info("executeCommandWithExpectedExitCode 方法成功情况测试完成")
    }
  }

  @Test
  fun `测试 executeCommandWithExpectedExitCode 方法的失败情况`() {
    log.info("开始测试 executeCommandWithExpectedExitCode 方法的失败情况")

    GenericContainer(DockerImageName.parse("alpine:3.21")).withCommand("sleep", "infinity").use { container ->
      container.start()

      val executor = ContainerCommandExecutor(container)

      assertFailsWith<AssertionError> { executor.executeCommandWithExpectedExitCode(0, commands = arrayOf("sh", "-c", "exit 1")) }

      log.info("executeCommandWithExpectedExitCode 方法失败情况测试完成")
    }
  }

  @Test
  fun `测试 executeCommandAndGetOutput 方法`() {
    log.info("开始测试 executeCommandAndGetOutput 方法")

    GenericContainer(DockerImageName.parse("alpine:3.21")).withCommand("sleep", "infinity").use { container ->
      container.start()

      val executor = ContainerCommandExecutor(container)
      val output = executor.executeCommandAndGetOutput(commands = arrayOf("echo", "Test Output"))

      assertEquals("Test Output", output, "输出应该与预期内容完全匹配（去除空白）")

      log.info("executeCommandAndGetOutput 方法测试完成")
    }
  }

  @Test
  fun `测试 executeCommandAndCheckOutput 方法的成功情况`() {
    log.info("开始测试 executeCommandAndCheckOutput 方法的成功情况")

    GenericContainer(DockerImageName.parse("alpine:3.21")).withCommand("sleep", "infinity").use { container ->
      container.start()

      val executor = ContainerCommandExecutor(container)
      val result = executor.executeCommandAndCheckOutput("Hello", commands = arrayOf("echo", "Hello World"))

      assertEquals(0, result.exitCode, "包含预期内容的命令应该成功")
      assertTrue(result.stdout.contains("Hello"), "输出应该包含检查的内容")

      log.info("executeCommandAndCheckOutput 方法成功情况测试完成")
    }
  }

  @Test
  fun `测试 executeCommandAndCheckOutput 方法的失败情况`() {
    log.info("开始测试 executeCommandAndCheckOutput 方法的失败情况")

    GenericContainer(DockerImageName.parse("alpine:3.21")).withCommand("sleep", "infinity").use { container ->
      container.start()

      val executor = ContainerCommandExecutor(container)

      assertFailsWith<AssertionError> { executor.executeCommandAndCheckOutput("NotFound", commands = arrayOf("echo", "Hello World")) }

      log.info("executeCommandAndCheckOutput 方法失败情况测试完成")
    }
  }

  @Test
  fun `测试 waitForContainerReady 方法`() {
    log.info("开始测试 waitForContainerReady 方法")

    GenericContainer(DockerImageName.parse("alpine:3.21")).withCommand("sleep", "infinity").use { container ->
      container.start()

      val executor = ContainerCommandExecutor(container)

      // 容器已经启动，等待就绪应该立即完成
      executor.waitForContainerReady(Duration.ofSeconds(5), Duration.ofMillis(100))

      assertTrue(container.isRunning, "容器应该处于运行状态")

      log.info("waitForContainerReady 方法测试完成")
    }
  }

  @Test
  fun `测试 fileExists 方法的存在文件`() {
    log.info("开始测试 fileExists 方法的存在文件")

    GenericContainer(DockerImageName.parse("alpine:3.21")).withCommand("sleep", "infinity").use { container ->
      container.start()

      val executor = ContainerCommandExecutor(container)

      assertTrue(executor.fileExists("/bin/sh"), "/bin/sh 文件应该存在")
      assertTrue(executor.fileExists("/etc/passwd"), "/etc/passwd 文件应该存在")

      log.info("fileExists 方法存在文件测试完成")
    }
  }

  @Test
  fun `测试 fileExists 方法的不存在文件`() {
    log.info("开始测试 fileExists 方法的不存在文件")

    GenericContainer(DockerImageName.parse("alpine:3.21")).withCommand("sleep", "infinity").use { container ->
      container.start()

      val executor = ContainerCommandExecutor(container)

      assertFalse(executor.fileExists("/nonexistent/file"), "不存在的文件应该返回 false")
      assertFalse(executor.fileExists("/tmp/does-not-exist"), "不存在的文件应该返回 false")

      log.info("fileExists 方法不存在文件测试完成")
    }
  }

  @Test
  fun `测试 waitForFile 方法`() {
    log.info("开始测试 waitForFile 方法")

    GenericContainer(DockerImageName.parse("alpine:3.21")).withCommand("sleep", "infinity").use { container ->
      container.start()

      val executor = ContainerCommandExecutor(container)

      // 创建文件
      executor.executeCommand(commands = arrayOf("touch", "/tmp/waitfile"))

      // 等待文件出现（文件已经存在，应该立即返回）
      executor.waitForFile("/tmp/waitfile", Duration.ofSeconds(5), Duration.ofMillis(100))

      assertTrue(executor.fileExists("/tmp/waitfile"), "等待的文件应该存在")

      log.info("waitForFile 方法测试完成")
    }
  }

  @Test
  fun `测试 readFileContent 方法`() {
    log.info("开始测试 readFileContent 方法")

    GenericContainer(DockerImageName.parse("alpine:3.21")).withCommand("sleep", "infinity").use { container ->
      container.start()

      val executor = ContainerCommandExecutor(container)

      // 创建一个测试文件
      executor.executeCommand(commands = arrayOf("sh", "-c", "echo 'Test File Content' > /tmp/testfile"))

      val content = executor.readFileContent("/tmp/testfile")

      assertEquals("Test File Content", content, "文件内容应该与预期匹配")

      log.info("readFileContent 方法测试完成")
    }
  }
}
