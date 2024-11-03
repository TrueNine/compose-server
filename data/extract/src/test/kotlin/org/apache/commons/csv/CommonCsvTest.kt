package org.apache.commons.csv

import jakarta.annotation.Resource
import net.yan100.compose.data.extract.DataExtractEntrance
import net.yan100.compose.testtookit.log
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.core.io.ClassPathResource
import org.springframework.core.io.ResourceLoader
import java.io.BufferedReader
import java.io.FileReader
import kotlin.test.Test

@SpringBootTest(classes = [DataExtractEntrance::class])
class CommonCsvTest {

  lateinit var resourceLoader: ResourceLoader @Resource set

  @Test
  fun `read csv file`() {
    val res = resourceLoader.getResource("classpath:area_code_2010.csv")

    val csvFile = ClassPathResource("config/data/area_code_2010.csv").file
    val csvReader = BufferedReader(FileReader(csvFile))
    val map = mutableMapOf<String, Long>()
    val csvParser = CSVParser.parse(csvReader, CSVFormat.DEFAULT)
    csvParser.records.forEach {
      val code = it[0]
      val idx = it.recordNumber
      map += code to idx
    }

    log.info("map size: {}", map.size)
    log.info("csv current line: {}", csvParser.recordNumber)
  }
}
