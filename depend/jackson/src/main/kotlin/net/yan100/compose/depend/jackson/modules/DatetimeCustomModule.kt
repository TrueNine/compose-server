package net.yan100.compose.depend.jackson.modules

import com.fasterxml.jackson.databind.module.SimpleDeserializers
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.databind.module.SimpleSerializers
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneOffset
import net.yan100.compose.depend.jackson.serializers.*

class DatetimeCustomModule(
  private val zoneOffset: ZoneOffset = ZoneOffset.ofHours(8)
) : SimpleModule() {

  override fun setupModule(context: SetupContext?) {
    super.setupModule(context)
    context?.addSerializers(
      SimpleSerializers(
        buildList {
          add(LocalDateSerializerX(zoneOffset))
          add(LocalDateTimeSerializerZ(zoneOffset))
          add(LocalTimeSerializerY(zoneOffset))
        }
      )
    )
    context?.addDeserializers(
      SimpleDeserializers(
        buildMap {
          put(LocalDate::class.java, LocalDateDeserializerX(zoneOffset))
          put(LocalDateTime::class.java, LocalDateTimeDeserializerZ(zoneOffset))
          put(LocalTime::class.java, LocalTimeDeserializerY(zoneOffset))
        }
      )
    )
  }
}
