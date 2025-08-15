package io.github.truenine.composeserver.depend.jackson.modules

import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.module.SimpleDeserializers
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.databind.module.SimpleSerializers
import io.github.truenine.composeserver.depend.jackson.serializers.*
import java.time.*

/**
 * 时间类型自定义模块
 *
 * 使用时间戳序列化器，将所有时间类型序列化为UTC时间戳（毫秒），确保时区无关性
 *
 * @author TrueNine
 * @since 2025-01-16
 */
class DatetimeCustomModule : SimpleModule() {

  override fun setupModule(context: SetupContext?) {
    super.setupModule(context)

    // 创建时间戳序列化器容器
    val serializers = ArrayList<JsonSerializer<*>>(6)

    // 注册所有时间类型的时间戳序列化器，确保时间戳序列化优先级最高
    serializers.add(LocalDateTimeTimestampSerializer())
    serializers.add(LocalDateTimestampSerializer())
    serializers.add(LocalTimeTimestampSerializer())
    serializers.add(InstantTimestampSerializer())
    serializers.add(ZonedDateTimeTimestampSerializer())
    serializers.add(OffsetDateTimeTimestampSerializer())

    // 添加到上下文
    context?.addSerializers(SimpleSerializers(serializers))

    // 创建时间戳反序列化器容器
    val deserializers = ArrayList<JsonDeserializer<*>>(6)

    deserializers.add(LocalDateTimeTimestampDeserializer())
    deserializers.add(LocalDateTimestampDeserializer())
    deserializers.add(LocalTimeTimestampDeserializer())
    deserializers.add(InstantTimestampDeserializer())
    deserializers.add(ZonedDateTimeTimestampDeserializer())
    deserializers.add(OffsetDateTimeTimestampDeserializer())

    context?.addDeserializers(
      SimpleDeserializers(
        buildMap {
          put(LocalDateTime::class.java, LocalDateTimeTimestampDeserializer())
          put(LocalDate::class.java, LocalDateTimestampDeserializer())
          put(LocalTime::class.java, LocalTimeTimestampDeserializer())
          put(Instant::class.java, InstantTimestampDeserializer())
          put(ZonedDateTime::class.java, ZonedDateTimeTimestampDeserializer())
          put(OffsetDateTime::class.java, OffsetDateTimeTimestampDeserializer())
        }
      )
    )
  }
}
