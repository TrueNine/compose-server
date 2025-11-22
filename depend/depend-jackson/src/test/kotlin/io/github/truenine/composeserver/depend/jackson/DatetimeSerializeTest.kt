package io.github.truenine.composeserver.depend.jackson

import io.github.truenine.composeserver.date
import io.github.truenine.composeserver.datetime
import io.github.truenine.composeserver.depend.jackson.autoconfig.JacksonAutoConfiguration
import io.github.truenine.composeserver.testtoolkit.log
import io.github.truenine.composeserver.time
import jakarta.annotation.Resource
import kotlin.test.Test
import org.springframework.boot.test.context.SpringBootTest
import tools.jackson.databind.ObjectMapper
import tools.jackson.module.kotlin.readValue

@SpringBootTest
class DatetimeSerializeTest {
  data class Obj(val d: date, val t: time, val dt: datetime)

  lateinit var mapper: ObjectMapper
    @Resource set

  lateinit var map: ObjectMapper
    @Resource(name = JacksonAutoConfiguration.NON_IGNORE_OBJECT_MAPPER_BEAN_NAME) set

  @Test
  fun `test serialize obj`() {
    val obj = Obj(d = date.now(), t = time.now(), dt = datetime.now())
    val json = mapper.writeValueAsString(obj)
    log.info(json)

    val nJson = map.writeValueAsString(obj)
    log.info(nJson)
    val nObj = map.readValue<Obj>(nJson)
    log.info("n obj: {}", nObj)
  }

  @Test
  fun `test serialize datetime`() {
    val d = datetime.now()
    val json = mapper.writeValueAsString(d)
    val nJson = map.writeValueAsString(d)
    log.info(json)
    log.info(nJson)
  }
}
