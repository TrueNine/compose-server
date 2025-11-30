package io.github.truenine.composeserver.consts

import io.github.truenine.composeserver.testtoolkit.log
import kotlin.test.*

/** Validates network address utilities exposed via {@link IInterAddr}. */
class IInterAddrTest {

  @Test
  fun exposesAllLocalHostIpAddresses() {
    log.info("Validating allLocalHostIP property")

    val allIps = IInterAddr.allLocalHostIP

    assertNotNull(allIps, "Local host IP list should not be null")
    log.info("Local host IP count: {}", allIps.size)

    allIps.forEachIndexed { index, ip ->
      log.info("Local IP[{}]: {}", index, ip)
      assertNotNull(ip, "IP address should not be null")
    }

    // Ensure the returned collection is a list
    assertTrue(allIps is List<String>, "Should return a List implementation")
  }
}
