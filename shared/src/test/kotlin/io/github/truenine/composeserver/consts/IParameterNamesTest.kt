package io.github.truenine.composeserver.consts

import io.github.truenine.composeserver.testtoolkit.log
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * # 请求参数名称常量测试
 *
 * 测试 IParameterNames 中定义的各种请求参数名称常量
 */
class IParameterNamesTest {

  @Test
  fun `测试租户 ID 参数常量`() {
    log.info("测试租户 ID 参数常量")

    assertEquals("x_group_code", IParameterNames.X_TENANT_ID)
    assertEquals("x_internal_tenant_id", IParameterNames.X_INTERNAL_TENANT_ID)

    log.info("X_TENANT_ID: {}", IParameterNames.X_TENANT_ID)
    log.info("X_INTERNAL_TENANT_ID: {}", IParameterNames.X_INTERNAL_TENANT_ID)
  }

  @Test
  fun `测试参数名称的命名规范`() {
    log.info("测试参数名称的命名规范")

    // 验证参数名都以 x_ 开头（小写）
    assertTrue(IParameterNames.X_TENANT_ID.startsWith("x_"), "X_TENANT_ID 应该以 x_ 开头")
    assertTrue(IParameterNames.X_INTERNAL_TENANT_ID.startsWith("x_"), "X_INTERNAL_TENANT_ID 应该以 x_ 开头")

    // 验证参数名使用下划线分隔
    assertTrue(IParameterNames.X_TENANT_ID.contains("_"), "X_TENANT_ID 应该包含下划线")
    assertTrue(IParameterNames.X_INTERNAL_TENANT_ID.contains("_"), "X_INTERNAL_TENANT_ID 应该包含下划线")

    log.info("验证了参数名称的命名规范")
  }

  @Test
  fun `测试参数名称的唯一性`() {
    log.info("测试参数名称的唯一性")

    val parameterNames = listOf(IParameterNames.X_TENANT_ID, IParameterNames.X_INTERNAL_TENANT_ID)

    val uniqueNames = parameterNames.toSet()

    assertEquals(parameterNames.size, uniqueNames.size, "所有参数名称应该是唯一的")

    log.info("验证了 {} 个参数名称的唯一性", parameterNames.size)
  }

  @Test
  fun `测试参数名称的小写规范`() {
    log.info("测试参数名称的小写规范")

    val parameterNames = listOf(IParameterNames.X_TENANT_ID, IParameterNames.X_INTERNAL_TENANT_ID)

    parameterNames.forEach { paramName ->
      assertEquals(paramName.lowercase(), paramName, "参数名应该是小写: $paramName")
      assertTrue(paramName.all { it.isLowerCase() || it == '_' }, "参数名应该只包含小写字母和下划线: $paramName")
    }

    log.info("验证了所有参数名称的小写规范")
  }

  @Test
  fun `测试租户相关参数的语义`() {
    log.info("测试租户相关参数的语义")

    // X_TENANT_ID 使用了 group_code 的命名，可能是历史原因
    assertTrue(IParameterNames.X_TENANT_ID.contains("group"), "X_TENANT_ID 包含 group 语义")
    assertTrue(IParameterNames.X_TENANT_ID.contains("code"), "X_TENANT_ID 包含 code 语义")

    // X_INTERNAL_TENANT_ID 明确表示内部租户 ID
    assertTrue(IParameterNames.X_INTERNAL_TENANT_ID.contains("internal"), "X_INTERNAL_TENANT_ID 包含 internal 语义")
    assertTrue(IParameterNames.X_INTERNAL_TENANT_ID.contains("tenant"), "X_INTERNAL_TENANT_ID 包含 tenant 语义")
    assertTrue(IParameterNames.X_INTERNAL_TENANT_ID.contains("id"), "X_INTERNAL_TENANT_ID 包含 id 语义")

    log.info("验证了租户相关参数的语义")
  }

  @Test
  fun `测试参数名称的长度合理性`() {
    log.info("测试参数名称的长度合理性")

    val parameterNames = listOf(IParameterNames.X_TENANT_ID, IParameterNames.X_INTERNAL_TENANT_ID)

    parameterNames.forEach { paramName ->
      assertTrue(paramName.length > 3, "参数名长度应该大于 3: $paramName")
      assertTrue(paramName.length < 50, "参数名长度应该小于 50: $paramName")
    }

    log.info("X_TENANT_ID 长度: {}", IParameterNames.X_TENANT_ID.length)
    log.info("X_INTERNAL_TENANT_ID 长度: {}", IParameterNames.X_INTERNAL_TENANT_ID.length)
  }

  @Test
  fun `测试参数名称不包含特殊字符`() {
    log.info("测试参数名称不包含特殊字符")

    val parameterNames = listOf(IParameterNames.X_TENANT_ID, IParameterNames.X_INTERNAL_TENANT_ID)

    val allowedChars = ('a'..'z').toSet() + '_'

    parameterNames.forEach { paramName -> assertTrue(paramName.all { it in allowedChars }, "参数名应该只包含小写字母和下划线: $paramName") }

    log.info("验证了参数名称不包含特殊字符")
  }

  @Test
  fun `测试参数名称的可读性`() {
    log.info("测试参数名称的可读性")

    // 验证参数名称具有良好的可读性
    assertTrue(IParameterNames.X_TENANT_ID.split("_").size >= 2, "X_TENANT_ID 应该有多个单词组成")
    assertTrue(IParameterNames.X_INTERNAL_TENANT_ID.split("_").size >= 3, "X_INTERNAL_TENANT_ID 应该有多个单词组成")

    // 验证不以下划线开头或结尾（除了 x_ 前缀）
    assertTrue(!IParameterNames.X_TENANT_ID.endsWith("_"), "参数名不应该以下划线结尾")
    assertTrue(!IParameterNames.X_INTERNAL_TENANT_ID.endsWith("_"), "参数名不应该以下划线结尾")

    log.info("验证了参数名称的可读性")
  }
}
