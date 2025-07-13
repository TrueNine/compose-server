package io.github.truenine.composeserver.consts

import io.github.truenine.composeserver.testtoolkit.log
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * # 数据库名称常量测试
 *
 * 测试 IDbNames 中定义的各种数据库相关常量
 */
class IDbNamesTest {

  @Test
  fun `测试租户相关常量`() {
    log.info("测试租户相关常量")

    assertEquals(0L, IDbNames.Tenant.ROOT_TENANT)
    assertEquals("0", IDbNames.Tenant.ROOT_TENANT_STR)
    assertEquals(0L, IDbNames.Tenant.DEFAULT_TENANT)
    assertEquals("0", IDbNames.Tenant.DEFAULT_TENANT_STR)

    // 验证租户常量与 RBAC 根 ID 的一致性
    assertEquals(IDbNames.Rbac.ROOT_ID, IDbNames.Tenant.ROOT_TENANT)
    assertEquals(IDbNames.Rbac.ROOT_ID, IDbNames.Tenant.DEFAULT_TENANT)

    log.info("ROOT_TENANT: {}", IDbNames.Tenant.ROOT_TENANT)
    log.info("ROOT_TENANT_STR: {}", IDbNames.Tenant.ROOT_TENANT_STR)
    log.info("DEFAULT_TENANT: {}", IDbNames.Tenant.DEFAULT_TENANT)
    log.info("DEFAULT_TENANT_STR: {}", IDbNames.Tenant.DEFAULT_TENANT_STR)
  }

  @Test
  fun `测试 RBAC 相关常量`() {
    log.info("测试 RBAC 相关常量")

    assertEquals(0L, IDbNames.Rbac.ROOT_ID)
    assertEquals("0", IDbNames.Rbac.ROOT_ID_STR)
    assertEquals(1L, IDbNames.Rbac.USER_ID)
    assertEquals("1", IDbNames.Rbac.USER_ID_STR)
    assertEquals(2L, IDbNames.Rbac.ADMIN_ID)
    assertEquals("2", IDbNames.Rbac.ADMIN_ID_STR)
    assertEquals(3L, IDbNames.Rbac.VIP_ID)
    assertEquals("3", IDbNames.Rbac.VIP_ID_STR)

    log.info("ROOT_ID: {}", IDbNames.Rbac.ROOT_ID)
    log.info("USER_ID: {}", IDbNames.Rbac.USER_ID)
    log.info("ADMIN_ID: {}", IDbNames.Rbac.ADMIN_ID)
    log.info("VIP_ID: {}", IDbNames.Rbac.VIP_ID)
  }

  @Test
  fun `测试 RBAC ID 字符串表示的一致性`() {
    log.info("测试 RBAC ID 字符串表示的一致性")

    assertEquals(IDbNames.Rbac.ROOT_ID.toString(), IDbNames.Rbac.ROOT_ID_STR)
    assertEquals(IDbNames.Rbac.USER_ID.toString(), IDbNames.Rbac.USER_ID_STR)
    assertEquals(IDbNames.Rbac.ADMIN_ID.toString(), IDbNames.Rbac.ADMIN_ID_STR)
    assertEquals(IDbNames.Rbac.VIP_ID.toString(), IDbNames.Rbac.VIP_ID_STR)

    log.info("验证了所有 RBAC ID 的字符串表示一致性")
  }

  @Test
  fun `测试 RBAC ID 的递增顺序`() {
    log.info("测试 RBAC ID 的递增顺序")

    assertTrue(IDbNames.Rbac.ROOT_ID < IDbNames.Rbac.USER_ID, "ROOT_ID 应该小于 USER_ID")
    assertTrue(IDbNames.Rbac.USER_ID < IDbNames.Rbac.ADMIN_ID, "USER_ID 应该小于 ADMIN_ID")
    assertTrue(IDbNames.Rbac.ADMIN_ID < IDbNames.Rbac.VIP_ID, "ADMIN_ID 应该小于 VIP_ID")

    val ids = listOf(IDbNames.Rbac.ROOT_ID, IDbNames.Rbac.USER_ID, IDbNames.Rbac.ADMIN_ID, IDbNames.Rbac.VIP_ID)

    val sortedIds = ids.sorted()
    assertEquals(ids, sortedIds, "RBAC ID 应该按递增顺序定义")

    log.info("RBAC ID 递增顺序: {}", ids)
  }

  @Test
  fun `测试租户和 RBAC 常量的关联性`() {
    log.info("测试租户和 RBAC 常量的关联性")

    // 验证租户的根 ID 与 RBAC 的根 ID 一致
    assertEquals(IDbNames.Rbac.ROOT_ID, IDbNames.Tenant.ROOT_TENANT)
    assertEquals(IDbNames.Rbac.ROOT_ID, IDbNames.Tenant.DEFAULT_TENANT)

    // 验证字符串表示也一致
    assertEquals(IDbNames.Rbac.ROOT_ID_STR, IDbNames.Tenant.ROOT_TENANT_STR)
    assertEquals(IDbNames.Rbac.ROOT_ID_STR, IDbNames.Tenant.DEFAULT_TENANT_STR)

    log.info("验证了租户和 RBAC 常量的关联性")
  }

  @Test
  fun `测试常量的非负性`() {
    log.info("测试常量的非负性")

    assertTrue(IDbNames.Rbac.ROOT_ID >= 0, "ROOT_ID 应该非负")
    assertTrue(IDbNames.Rbac.USER_ID >= 0, "USER_ID 应该非负")
    assertTrue(IDbNames.Rbac.ADMIN_ID >= 0, "ADMIN_ID 应该非负")
    assertTrue(IDbNames.Rbac.VIP_ID >= 0, "VIP_ID 应该非负")
    assertTrue(IDbNames.Tenant.ROOT_TENANT >= 0, "ROOT_TENANT 应该非负")
    assertTrue(IDbNames.Tenant.DEFAULT_TENANT >= 0, "DEFAULT_TENANT 应该非负")

    log.info("验证了所有 ID 常量的非负性")
  }

  @Test
  fun `测试常量的唯一性`() {
    log.info("测试常量的唯一性")

    val rbacIds = setOf(IDbNames.Rbac.ROOT_ID, IDbNames.Rbac.USER_ID, IDbNames.Rbac.ADMIN_ID, IDbNames.Rbac.VIP_ID)

    assertEquals(4, rbacIds.size, "所有 RBAC ID 应该是唯一的")

    val rbacIdStrs = setOf(IDbNames.Rbac.ROOT_ID_STR, IDbNames.Rbac.USER_ID_STR, IDbNames.Rbac.ADMIN_ID_STR, IDbNames.Rbac.VIP_ID_STR)

    assertEquals(4, rbacIdStrs.size, "所有 RBAC ID 字符串应该是唯一的")

    log.info("验证了 RBAC ID 的唯一性")
  }
}

private fun assertTrue(condition: Boolean, message: String) {
  kotlin.test.assertTrue(condition, message)
}
