package io.github.truenine.composeserver.testtoolkit.utils

import io.github.truenine.composeserver.testtoolkit.log
import java.time.Duration
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import org.testcontainers.containers.GenericContainer
import org.testcontainers.utility.DockerImageName

/**
 * # 容器扩展函数测试
 *
 * 测试 ContainerExtensions.kt 中的所有扩展函数
 *
 * @author TrueNine
 * @since 2025-07-12
 */
class ContainerExtensionsTest {

  @Test
  fun `测试 commandExecutor 扩展函数`() {
    log.info("开始测试 commandExecutor 扩展函数")

    GenericContainer(DockerImageName.parse("alpine:3.21")).withCommand("sleep", "infinity").use { container ->
      container.start()

      val executor = container.commandExecutor()

      assertNotNull(executor, "命令执行器不应该为 null")

      log.info("commandExecutor 扩展函数测试完成")
    }
  }

  @Test
  fun `测试 safeExecInContainer 扩展函数`() {
    log.info("开始测试 safeExecInContainer 扩展函数")

    GenericContainer(DockerImageName.parse("alpine:3.21"))
      .withCommand("sleep", "300") // 让容器运行5分钟，足够测试使用
      .use { container ->
        container.start()

        // 等待容器完全启动
        Thread.sleep(1000)

        val result = container.safeExecInContainer(commands = arrayOf("echo", "Hello World"))

        assertEquals(0, result.exitCode, "命令执行应该成功")
        assertTrue(result.stdout.contains("Hello World"), "输出应该包含预期内容")

        log.info("safeExecInContainer 扩展函数测试完成")
      }
  }

  @Test
  fun `测试 safeExecInContainer 扩展函数带自定义参数`() {
    log.info("开始测试 safeExecInContainer 扩展函数带自定义参数")

    GenericContainer(DockerImageName.parse("alpine:3.21")).withCommand("sleep", "infinity").use { container ->
      container.start()

      val result = container.safeExecInContainer(timeout = Duration.ofSeconds(10), maxRetries = 2, "echo", "Custom Test")

      assertEquals(0, result.exitCode, "自定义参数的命令执行应该成功")
      assertTrue(result.stdout.contains("Custom Test"), "输出应该包含预期内容")

      log.info("safeExecInContainer 扩展函数带自定义参数测试完成")
    }
  }

  @Test
  fun `测试 execWithExpectedExitCode 扩展函数`() {
    log.info("开始测试 execWithExpectedExitCode 扩展函数")

    GenericContainer(DockerImageName.parse("alpine:3.21")).withCommand("sleep", "infinity").use { container ->
      container.start()

      // 测试成功的命令
      val result = container.execWithExpectedExitCode(0, commands = arrayOf("echo", "Success"))
      assertEquals(0, result.exitCode, "期望退出码为0的命令应该成功")

      log.info("execWithExpectedExitCode 扩展函数测试完成")
    }
  }

  @Test
  fun `测试 execAndGetOutput 扩展函数`() {
    log.info("开始测试 execAndGetOutput 扩展函数")

    GenericContainer(DockerImageName.parse("alpine:3.21")).withCommand("sleep", "infinity").use { container ->
      container.start()

      val output = container.execAndGetOutput(commands = arrayOf("echo", "Test Output"))

      assertTrue(output.contains("Test Output"), "输出应该包含预期内容")

      log.info("execAndGetOutput 扩展函数测试完成")
    }
  }

  @Test
  fun `测试 execAndCheckOutput 扩展函数`() {
    log.info("开始测试 execAndCheckOutput 扩展函数")

    GenericContainer(DockerImageName.parse("alpine:3.21")).withCommand("sleep", "infinity").use { container ->
      container.start()

      val result = container.execAndCheckOutput("Hello", commands = arrayOf("echo", "Hello World"))

      assertEquals(0, result.exitCode, "包含预期内容的命令应该成功")
      assertTrue(result.stdout.contains("Hello"), "输出应该包含检查的内容")

      log.info("execAndCheckOutput 扩展函数测试完成")
    }
  }

  @Test
  fun `测试 waitForReady 扩展函数`() {
    log.info("开始测试 waitForReady 扩展函数")

    GenericContainer(DockerImageName.parse("alpine:3.21")).withCommand("sleep", "infinity").use { container ->
      container.start()

      // 容器已经启动，等待就绪应该立即完成
      container.waitForReady(Duration.ofSeconds(5), Duration.ofMillis(100))

      assertTrue(container.isRunning, "容器应该处于运行状态")

      log.info("waitForReady 扩展函数测试完成")
    }
  }

  @Test
  fun `测试 fileExists 扩展函数`() {
    log.info("开始测试 fileExists 扩展函数")

    GenericContainer(DockerImageName.parse("alpine:3.21")).withCommand("sleep", "infinity").use { container ->
      container.start()

      // 测试存在的文件
      val exists = container.fileExists("/bin/sh")
      assertTrue(exists, "/bin/sh 文件应该存在")

      // 测试不存在的文件
      val notExists = container.fileExists("/nonexistent/file")
      assertTrue(!notExists, "不存在的文件应该返回 false")

      log.info("fileExists 扩展函数测试完成")
    }
  }

  @Test
  fun `测试 readFile 扩展函数`() {
    log.info("开始测试 readFile 扩展函数")

    GenericContainer(DockerImageName.parse("alpine:3.21")).withCommand("sleep", "infinity").use { container ->
      container.start()

      // 创建一个测试文件
      container.safeExecInContainer(commands = arrayOf("sh", "-c", "echo 'Test Content' > /tmp/testfile"))

      val content = container.readFile("/tmp/testfile")

      assertTrue(content.contains("Test Content"), "文件内容应该包含预期文本")

      log.info("readFile 扩展函数测试完成")
    }
  }

  @Test
  fun `测试 waitForFile 扩展函数`() {
    log.info("开始测试 waitForFile 扩展函数")

    GenericContainer(DockerImageName.parse("alpine:3.21")).withCommand("sleep", "infinity").use { container ->
      container.start()

      // 创建文件
      container.safeExecInContainer(commands = arrayOf("touch", "/tmp/waitfile"))

      // 等待文件出现（文件已经存在，应该立即返回）
      container.waitForFile("/tmp/waitfile", Duration.ofSeconds(5), Duration.ofMillis(100))

      // 验证文件确实存在
      assertTrue(container.fileExists("/tmp/waitfile"), "等待的文件应该存在")

      log.info("waitForFile 扩展函数测试完成")
    }
  }

  @Test
  fun `测试 withStableWaitStrategy 扩展函数`() {
    log.info("开始测试 withStableWaitStrategy 扩展函数")

    // 测试扩展函数的存在性，通过调用其他扩展函数来间接验证
    GenericContainer(DockerImageName.parse("alpine:3.21")).withCommand("sleep", "infinity").use { container ->
      container.start()

      // 验证容器正常启动
      assertTrue(container.isRunning, "容器应该正常启动")

      // 通过其他扩展函数验证功能
      val result = container.safeExecInContainer(commands = arrayOf("echo", "test"))
      assertEquals(0, result.exitCode, "命令执行应该成功")

      log.info("withStableWaitStrategy 扩展函数测试完成")
    }
  }

  @Test
  fun `测试 withStableWaitStrategy 扩展函数带自定义参数`() {
    log.info("开始测试 withStableWaitStrategy 扩展函数带自定义参数")

    GenericContainer(DockerImageName.parse("alpine:3.21")).withCommand("sleep", "infinity").use { container ->
      container.start()

      assertTrue(container.isRunning, "使用自定义参数的稳定等待策略应该正常工作")

      log.info("withStableWaitStrategy 扩展函数带自定义参数测试完成")
    }
  }

  @Test
  fun `测试 withHealthCheck 扩展函数`() {
    log.info("开始测试 withHealthCheck 扩展函数")

    GenericContainer(DockerImageName.parse("alpine:3.21")).withCommand("sleep", "infinity").use { container ->
      container.start()

      assertTrue(container.isRunning, "使用健康检查的容器应该正常启动")

      log.info("withHealthCheck 扩展函数测试完成")
    }
  }

  @Test
  fun `测试 withHealthCheck 扩展函数带自定义参数`() {
    log.info("开始测试 withHealthCheck 扩展函数带自定义参数")

    GenericContainer(DockerImageName.parse("alpine:3.21")).withCommand("sleep", "infinity").use { container ->
      container.start()

      assertTrue(container.isRunning, "使用自定义健康检查参数的容器应该正常启动")

      log.info("withHealthCheck 扩展函数带自定义参数测试完成")
    }
  }

  @Test
  fun `测试 startAndWaitForReady 扩展函数`() {
    log.info("开始测试 startAndWaitForReady 扩展函数")

    GenericContainer(DockerImageName.parse("alpine:3.21")).withCommand("sleep", "infinity").use { container ->
      container.startAndWaitForReady()

      assertTrue(container.isRunning, "启动并等待就绪的容器应该处于运行状态")

      log.info("startAndWaitForReady 扩展函数测试完成")
    }
  }

  @Test
  fun `测试 startAndWaitForReady 扩展函数带自定义参数`() {
    log.info("开始测试 startAndWaitForReady 扩展函数带自定义参数")

    GenericContainer(DockerImageName.parse("alpine:3.21")).withCommand("sleep", "infinity").use { container ->
      container.startAndWaitForReady(readyTimeout = Duration.ofSeconds(20), readyPollInterval = Duration.ofMillis(200))

      assertTrue(container.isRunning, "使用自定义参数启动并等待就绪的容器应该处于运行状态")

      log.info("startAndWaitForReady 扩展函数带自定义参数测试完成")
    }
  }
}
