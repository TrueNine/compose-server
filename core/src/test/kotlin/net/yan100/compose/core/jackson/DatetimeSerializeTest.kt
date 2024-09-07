package net.yan100.compose.core.jackson

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import net.yan100.compose.core.alias.date
import net.yan100.compose.core.alias.datetime
import net.yan100.compose.core.alias.time
import net.yan100.compose.core.autoconfig.JacksonSerializationAutoConfig
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import kotlin.test.Test


@WebMvcTest
class DatetimeSerializeTest {
  data class Obj(
    val d: date,
    val t: time,
    val dt: datetime
  )

  @Autowired
  private lateinit var mapper: ObjectMapper

  @Autowired
  @Qualifier(JacksonSerializationAutoConfig.NON_IGNORE_OBJECT_MAPPER_BEAN_NAME)
  private lateinit var map: ObjectMapper

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
