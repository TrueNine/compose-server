package net.yan100.compose.data.extract.service.impl

import jakarta.annotation.Resource
import net.yan100.compose.testtookit.log
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import org.springframework.boot.test.context.SpringBootTest
import kotlin.test.*

@SpringBootTest
class LazyAddressCsvServiceImplTest {
  lateinit var service: LazyAddressCsvServiceImpl @Resource set

  @BeforeTest
  fun setup() {
    service.csvVersions += "2010" to "area_code_2010.csv"
    service.csvVersions += "2018" to "area_code_2018.csv"
  }

  @Test
  fun `test find all city by code`() {
    val beijing = service.findAllCityByCode("11")
    assertTrue { beijing.isNotEmpty() }
    assertEquals(1, beijing.size)
  }

  @Test
  fun `test find all province`() {
    val all = service.findAllProvinces()
    log.info("all province: {}", all)
    assertTrue { all.isNotEmpty() }
    assertEquals(31, all.size)
  }

  @Test
  fun `test get line sequence`() {
    val seq = service.getCsvSequence("2018")
    assertNotNull(seq)
    val line = seq.first()
    log.info("first line: {}", line)
    assertNotNull(line)
  }

  @Test
  fun `test get csv resource`() {
    val resource = service.getCsvResource("2018")
    assertNotNull(resource)
    log.info("resource: {}", resource)
    assertTrue { resource.exists() }
    resource.inputStream.bufferedReader().use {
      val records = CSVParser.parse(it, CSVFormat.DEFAULT).records
      assertTrue { records.isNotEmpty() }
    }
  }

  @Test
  fun `get last year version`() {
    service.csvVersions += "1010" to "2010.csv"
    service.csvVersions += "1017" to "2017.csv"
    service.csvVersions += "1018" to "2018.csv"
    val yearVersion = service.lastYearVersion
    assertEquals("2018", yearVersion)
  }
}
