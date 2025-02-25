package net.yan100.compose.data.extract.service

import jakarta.annotation.Resource
import kotlin.test.*
import net.yan100.compose.testtookit.assertEmpty
import net.yan100.compose.testtookit.assertNotEmpty
import net.yan100.compose.testtookit.log
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.core.io.ResourceLoader

@SpringBootTest
class ILazyAddressServiceTest {
  lateinit var service: ILazyAddressService
    @Resource set

  @Test
  fun `test find history data`() {
    val b =
      service.lookupAllChildrenByCode(
        "653126201",
        firstFind = {
          println("first find")
          null
        },
        deepCondition = { false },
      ) {
        it.result
      }
    assertNotEmpty { b }
    assertEquals("2023", b.first().yearVersion)
  }

  @Test
  fun `test lockup not found`() {
    val a =
      service.lookupAllChildrenByCode(
        "133025",
        firstFind = {
          println("first find")
          null
        },
        deepCondition = { false },
      ) {
        it.result
      }
    assertEmpty { a }
  }

  @Test
  fun `test find children`() {
    val a = service.findAllChildrenByCode("4331")
    assertFalse(a.isEmpty())
    assertTrue { a.all { it.code.code.startsWith("4331") } }
    assertFailsWith<IllegalArgumentException> {
      service.findAllChildrenByCode("4")
    }

    assertFailsWith<IllegalArgumentException> {
      service.findAllChildrenByCode("433")
    }
    val b = service.findAllChildrenByCode("")
    assertEquals(31, b.size)
    assertTrue { b.all { it.level == 1 } }
  }

  @Test
  fun `test lookupByCode`() {
    val a =
      service.lookupByCode("11") {
        it.result.find { ir -> ir.code.code == "11" }
      }
    assertNotNull(a)
    assertEquals("11", a.code.code)
    assertEquals("北京市", a.name)
    val b =
      service.lookupByCode("1101") {
        it.result.find { ir -> ir.code.code == "1101" }
      }
    assertNotNull(b)
    assertEquals("1101", b.code.code)
    assertEquals("市辖区", b.name)

    val c =
      service.lookupByCode("99") {
        it.result.find { ir -> ir.code.code == "99" }
      }
    assertNull(c)

    val d =
      service.lookupByCode("370125") {
        it.result.find { ir -> ir.code.code == "370125" }
      }
    assertNotNull(d)
    assertEquals("370125", d.code.code)
  }

  @Test
  fun `test lookupAllChildrenByCode`() {
    val a =
      service.lookupAllChildrenByCode(
        "433127103",
        firstFind = { null },
        deepCondition = { false },
      ) {
        it.result
      }
    assertTrue(a.isNotEmpty())
    val b =
      service.lookupAllChildrenByCode(
        "433127103221",
        firstFind = { null },
        deepCondition = { false },
      ) {
        it.result
      }
    assertFalse(b.isNotEmpty())
  }

  @Test
  fun `look sorted save test`() {
    val r =
      service.lookupAllChildrenByCode("370125000000") {
        val re = it.result
        log.info("save parent code: {}", it.parentCode)
        log.info("save parent code: {}", it.notInit)
        log.info("save result: {}", re)
        re
      }
  }

  lateinit var resourceLoader: ResourceLoader
    @Resource set

  @Ignore
  @Test
  fun a() {
    resourceLoader
      .getResource("classpath:config/data/area_code_2024.csv")
      .inputStream
      .bufferedReader()
      .use { ab ->
        resourceLoader
          .getResource("classpath:config/data/area_code_2023.csv")
          .inputStream
          .bufferedReader()
          .use { bb ->
            val a =
              CSVParser.parse(ab, CSVFormat.DEFAULT)
                .records
                .map { it.get(0).toLong() }
                .toMutableSet()
            val b =
              CSVParser.parse(bb, CSVFormat.DEFAULT)
                .records
                .map { it.get(0).toLong() }
                .toMutableSet()
            log.info("loaded")
            log.info("a size: {}", a.size)
            log.info("a size: {}", b.size)

            val result =
              b.filter {
                val isr = it !in a
                if (!isr) {
                  a.remove(it)
                  log.info("a removed size: {}", a.size)
                }
                isr
              }
            result.forEach { log.info("it: {}", it) }
          }
      }
  }

  @Test
  fun `test get lastYearVersionOrNull`() {
    log.info("last version: {}", service.lastYearVersion)
    assertTrue { service.supportedYearVersions.size > 1 }

    log.info("supported versions: {}", service.supportedYearVersions)
    val nextVersion = service.supportedYearVersions.sorted().reversed()[1]
    log.info("next version: {}", nextVersion)
    assertEquals(
      nextVersion,
      service.lastYearVersionOrNull(service.lastYearVersion),
    )
  }
}
