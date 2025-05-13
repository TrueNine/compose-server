package net.yan100.compose.testtoolkit

import org.junit.jupiter.api.io.TempDir
import java.io.File
import java.nio.file.Path
import java.util.*
import kotlin.test.*

/** 确保能够启动 */
class GradleEnvironmentTest {

  @BeforeTest
  fun setup() {
    assertNotNull(log)
  }

  @Test
  fun `gradle 读取到了 project env 文件`() {
    val e = System.getenv("TEST_A")
    assertNotNull(e)
    assertEquals(e, "1")
  }

  @Test
  fun `确保 能够 加载到 gradle 环境变量`() {
    val e = System.getenv("ENV_FILE_EMPTY")
    assertNull(e)
  }

  @Test
  fun `确保 测试类 能够 正常启动`() {
    log.info("launched")
  }

  /**
   * 递归向上查找 gradle.properties 文件，返回 File 或 null
   */
  fun findGradleProperties(): File? {
    var dir: File? = File(System.getProperty("user.dir"))
    while (dir != null) {
      val f = File(dir, "gradle.properties")
      if (f.exists()) return f
      dir = dir.parentFile
    }
    return null
  }

  @Test
  fun `gradle properties 配置的 gpg 能签名且密钥文件存在`(@TempDir tempDir: Path) {
    // 1. 查找 gradle.properties
    val gradlePropFile = assertNotNull(findGradleProperties(), "gradle.properties 文件不存在，无法获取签名配置！")
    log.info("找到 gradle.properties 路径: {}", gradlePropFile.absolutePath)
    // 2. 读取 gradle.properties 配置
    val props = Properties()
    gradlePropFile.inputStream().use { props.load(it) }
    val keyId = assertNotNull(props.getProperty("signing.keyId"), "gradle.properties 未配置 signing.keyId")
    val keyFilePath = assertNotNull(props.getProperty("signing.secretKeyRingFile"), "gradle.properties 未配置 signing.secretKeyRingFile")
    log.info("读取到 keyId: {}, keyFilePath: {}", keyId, keyFilePath)
    val keyFile = File(keyFilePath)
    assertTrue(keyFile.exists(), "GPG 密钥文件 $keyFilePath 不存在，请检查 gradle.properties 配置！")
    log.info("GPG 密钥文件存在: {}", keyFile.absolutePath)

    // 断言gpg命令行存在
    log.info("检查 gpg 命令行工具是否可用...")
    val gpgExists = try {
      val which = if (System.getProperty("os.name").lowercase().contains("win")) "where" else "which"
      val proc = ProcessBuilder(which, "gpg").redirectErrorStream(true).start()
      val result = proc.waitFor() == 0
      log.info("gpg 检查结果: {}", result)
      result
    } catch (e: Exception) {
      log.error("gpg 检查异常", e)
      false
    }
    assertTrue(gpgExists, "gpg 命令行工具未安装或未在 PATH 中！")

    // 3. 用@TempDir创建临时文件
    val testFile = tempDir.resolve("gpg-sign-test.txt").toFile()
    val testContent = "gpg-sign-test-${UUID.randomUUID()}"
    testFile.writeText(testContent)
    log.info("创建待签名临时文件: {}", testFile.absolutePath)
    val signedFile = File(testFile.absolutePath + ".gpg")
    val signCmd = listOf(
      "gpg", "--yes", "--batch", "--output", signedFile.absolutePath,
      "--sign", "--default-key", keyId, testFile.absolutePath
    )
    log.info("执行签名命令: {}", signCmd.joinToString(" "))
    val process = ProcessBuilder(signCmd)
      .redirectErrorStream(true)
      .start()
    val output = process.inputStream.bufferedReader().readText()
    val exitCode = process.waitFor()
    log.info("gpg 签名命令返回码: {}, 输出: {}", exitCode, output)
    assertEquals(0, exitCode, "gpg 签名失败: $output")
    assertTrue(signedFile.exists(), "签名文件未生成，gpg 执行异常")
    log.info("签名文件生成成功: {}", signedFile.absolutePath)
  }
}
