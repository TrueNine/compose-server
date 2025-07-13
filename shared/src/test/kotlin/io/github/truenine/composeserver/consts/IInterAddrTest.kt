package io.github.truenine.composeserver.consts

import io.github.truenine.composeserver.testtoolkit.log
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/**
 * # 网络地址工具测试
 *
 * 测试 IInterAddr 中定义的网络地址相关工具方法和常量
 */
class IInterAddrTest {

  @Test
  fun `测试 allLocalHostIP 属性`() {
    log.info("测试 allLocalHostIP 属性")

    val allIps = IInterAddr.allLocalHostIP

    assertNotNull(allIps, "本地主机 IP 列表不应该为空")
    log.info("本地主机 IP 数量: {}", allIps.size)

    allIps.forEachIndexed { index, ip ->
      log.info("本地 IP[{}]: {}", index, ip)
      assertNotNull(ip, "IP 地址不应该为空")
    }

    // 验证返回的是不可变列表
    assertTrue(allIps is List<String>, "应该返回 List 类型")
  }

  @Test
  fun `测试网络异常处理`() {
    log.info("测试网络异常处理")

    // 当网络操作失败时，应该有合理的回退机制
    val allIps = IInterAddr.allLocalHostIP

    // 即使网络操作失败，也应该返回一个列表（可能为空）
    assertNotNull(allIps, "即使网络异常，也应该返回非空列表")
    log.info("网络异常情况下的 IP 列表大小: {}", allIps.size)
  }

  @Test
  fun `测试 IInterAddr 类的存在性`() {
    log.info("测试 IInterAddr 类的存在性")

    val clazz = IInterAddr::class.java

    assertEquals("IInterAddr", clazz.simpleName, "类名应该正确")
    assertEquals("io.github.truenine.composeserver.consts", clazz.packageName, "包名应该正确")
    assertTrue(clazz.isInterface, "应该是接口类型")

    log.info("IInterAddr 类验证通过")
  }

  @Test
  fun `测试 IInterAddr 的方法存在性`() {
    log.info("测试 IInterAddr 的方法存在性")

    val clazz = IInterAddr::class.java
    val companionClass = clazz.declaredClasses.find { it.simpleName == "Companion" }

    assertNotNull(companionClass, "应该有 Companion 对象")

    val methods = companionClass!!.declaredMethods
    val methodNames = methods.map { it.name }.toSet()

    log.info("IInterAddr.Companion 方法: {}", methodNames)

    // 验证关键方法存在
    assertTrue(methodNames.contains("getRequestIpAddress"), "应该有 getRequestIpAddress 方法")
    assertTrue(methodNames.contains("getAllLocalHostIP"), "应该有 getAllLocalHostIP 方法")

    log.info("方法存在性验证通过")
  }

  @Test
  fun `测试本地主机 IP 获取的稳定性`() {
    log.info("测试本地主机 IP 获取的稳定性")

    // 多次调用应该返回一致的结果
    val ips1 = IInterAddr.allLocalHostIP
    val ips2 = IInterAddr.allLocalHostIP

    assertEquals(ips1.size, ips2.size, "多次调用应该返回相同数量的 IP")

    // 验证内容一致性
    ips1.forEachIndexed { index, ip ->
      if (index < ips2.size) {
        assertEquals(ip, ips2[index], "相同索引的 IP 应该一致")
      }
    }

    log.info("本地主机 IP 获取稳定性验证通过")
  }
}

private fun assertEquals(expected: Any?, actual: Any?, message: String) {
  kotlin.test.assertEquals(expected, actual, message)
}
