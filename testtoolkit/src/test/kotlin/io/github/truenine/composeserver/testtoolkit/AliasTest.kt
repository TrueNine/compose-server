package io.github.truenine.composeserver.testtoolkit

import java.nio.file.Path
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import org.junit.jupiter.api.io.TempDir
import org.slf4j.LoggerFactory
import org.springframework.test.annotation.Rollback

/**
 * # 类型别名测试
 *
 * 测试 Alias.kt 中定义的类型别名，确保它们正确映射到对应的类型
 *
 * @author TrueNine
 * @since 2025-07-12
 */
class AliasTest {

  @Test
  fun `测试 SysLogger 类型别名`() {
    log.info("开始测试 SysLogger 类型别名")

    // 验证 SysLogger 类型别名指向正确的类型
    val logger: SystemTestLogger = LoggerFactory.getLogger(AliasTest::class.java)
    assertNotNull(logger, "SysLogger 应该能够正确创建")

    // 验证基本功能
    logger.info("测试 SysLogger 类型别名功能")
    logger.debug("测试 debug 级别日志")
    logger.trace("测试 trace 级别日志")

    log.info("SysLogger 类型别名测试完成")
  }

  @Test
  fun `测试 RDBRollback 类型别名`() {
    log.info("开始测试 RDBRollback 类型别名")

    // 验证 RDBRollback 类型别名指向正确的类型
    val rollbackAnnotation = RDBRollback::class
    assertEquals(Rollback::class, rollbackAnnotation, "RDBRollback 应该指向 Rollback 注解")

    // 验证注解的基本属性
    val rollbackInstance =
      object {
        @RDBRollback fun testMethod() {}
      }

    val method = rollbackInstance::class.java.getDeclaredMethod("testMethod")
    val annotation = method.getAnnotation(RDBRollback::class.java)
    assertNotNull(annotation, "RDBRollback 注解应该能够正确应用")

    // 验证默认值
    assertTrue(annotation.value, "RDBRollback 注解的默认值应该为 true")

    log.info("RDBRollback 类型别名测试完成")
  }

  @Test
  fun `测试 TempDirMapping 类型别名`() {
    log.info("开始测试 TempDirMapping 类型别名")

    // 验证 TempDirMapping 类型别名指向正确的类型
    val tempDirAnnotation = TempDirMapping::class
    assertEquals(TempDir::class, tempDirAnnotation, "TempDirMapping 应该指向 TempDir 注解")

    // 创建一个使用 TempDirMapping 注解的测试方法
    val testInstance =
      object {
        fun testMethodWithTempDir(@TempDirMapping tempDir: Path) {
          // 验证临时目录功能
          assertTrue(tempDir.toFile().exists(), "临时目录应该存在")
          assertTrue(tempDir.toFile().isDirectory(), "临时目录应该是一个目录")
        }
      }

    // 验证注解能够正确应用
    val method = testInstance::class.java.getDeclaredMethod("testMethodWithTempDir", Path::class.java)
    val parameters = method.parameters
    assertTrue(parameters.isNotEmpty(), "方法应该有参数")

    val tempDirParam = parameters[0]
    val annotation = tempDirParam.getAnnotation(TempDirMapping::class.java)
    assertNotNull(annotation, "TempDirMapping 注解应该能够正确应用到参数上")

    log.info("TempDirMapping 类型别名测试完成")
  }

  @Test
  fun `测试所有类型别名的包导入`() {
    log.info("开始测试所有类型别名的包导入")

    // 验证所有类型别名都能正确导入和使用
    val sysLoggerClass = SystemTestLogger::class.java
    val rollbackClass = RDBRollback::class.java
    val tempDirClass = TempDirMapping::class.java

    // 验证类名
    assertEquals("org.slf4j.Logger", sysLoggerClass.name, "SysLogger 应该指向正确的类")
    assertEquals("org.springframework.test.annotation.Rollback", rollbackClass.name, "RDBRollback 应该指向正确的类")
    assertEquals("org.junit.jupiter.api.io.TempDir", tempDirClass.name, "TempDirMapping 应该指向正确的类")

    log.info("所有类型别名的包导入测试完成")
  }

  @Test
  fun `测试类型别名在实际使用场景中的兼容性`() {
    log.info("开始测试类型别名在实际使用场景中的兼容性")

    // 测试 SysLogger 在实际日志记录中的使用
    val logger: SystemTestLogger = LoggerFactory.getLogger("test.logger")
    logger.info("测试实际日志记录功能")

    // 测试类型推断
    val inferredLogger = LoggerFactory.getLogger(AliasTest::class.java)
    assertTrue(inferredLogger is SystemTestLogger, "类型推断应该正确")

    // 测试方法调用兼容性
    fun acceptSysLogger(logger: SystemTestLogger) {
      logger.info("接受 SysLogger 参数的方法")
    }

    acceptSysLogger(logger)
    acceptSysLogger(inferredLogger)

    log.info("类型别名在实际使用场景中的兼容性测试完成")
  }
}
