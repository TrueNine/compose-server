package net.yan100.compose.depend.jackson.modules

import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.module.SimpleDeserializers
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.databind.module.SimpleSerializers
import net.yan100.compose.depend.jackson.serializers.ISO8601Deserializer
import net.yan100.compose.depend.jackson.serializers.ISO8601Serializer
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneOffset

class DatetimeCustomModule(
  private val zoneOffset: ZoneOffset = ZoneOffset.ofHours(8)
) : SimpleModule() {

  override fun setupModule(context: SetupContext?) {
    super.setupModule(context)

    // 创建自定义序列化器容器
    val serializers = ArrayList<JsonSerializer<*>>(3)

    // 使用无参构造函数创建序列化器
    val dateSerializer = ISO8601Serializer.ISO8601DateSerializer()
    val dateTimeSerializer = ISO8601Serializer.ISO8601DateTimeSerializer()
    val timeSerializer = ISO8601Serializer.ISO8601TimeSerializer()

    // 添加到列表
    serializers.add(dateSerializer)
    serializers.add(dateTimeSerializer)
    serializers.add(timeSerializer)

    // 添加到上下文
    context?.addSerializers(SimpleSerializers(serializers))

    context?.addDeserializers(
      SimpleDeserializers(
        buildMap {
          put(LocalDate::class.java, ISO8601Deserializer.LocalDateDeserializerX(zoneOffset))
          put(LocalDateTime::class.java, ISO8601Deserializer.LocalDateTimeDeserializerZ(zoneOffset))
          put(LocalTime::class.java, ISO8601Deserializer.LocalTimeDeserializerY(zoneOffset))
        }
      )
    )
  }
}
