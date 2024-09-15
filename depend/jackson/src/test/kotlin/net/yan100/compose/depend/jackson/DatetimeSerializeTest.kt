package net.yan100.compose.depend.jackson

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import jakarta.annotation.Resource
import net.yan100.compose.core.date
import net.yan100.compose.core.datetime
import net.yan100.compose.core.time
import net.yan100.compose.depend.jackson.autoconfig.JacksonAutoConfiguration
import org.springframework.boot.test.context.SpringBootTest
import kotlin.test.Test


@SpringBootTest
class DatetimeSerializeTest {
  data class Obj(
    val d: date,
    val t: time,
    val dt: datetime
  )

  lateinit var mapper: ObjectMapper
    @Resource set

  lateinit var map: ObjectMapper
    @Resource(name = JacksonAutoConfiguration.NON_IGNORE_OBJECT_MAPPER_BEAN_NAME)
    set

  @Test
  fun `test serialize obj`() {
    val obj = Obj(
      d = date.now(),
      t = time.now(),
      dt = datetime.now()
    )
    val json = mapper.writeValueAsString(obj)
    println(json)

    val nJson = map.writeValueAsString(obj)
    println(nJson)
    val nObj = map.readValue<Obj>(nJson)
    println(nObj)
  }

  @Test
  fun `test serialize datetime`() {
    val d = datetime.now()
    val json = mapper.writeValueAsString(d)
    val nJson = map.writeValueAsString(d)
    println(json)
    println(nJson)
  }
}
