/*
 * ## Copyright (c) 2024 TrueNine. All rights reserved.
 *
 * The following source code is owned, developed and copyrighted by TrueNine
 * (truenine304520@gmail.com) and represents a substantial investment of time, effort,
 * and resources. This software and its components are not to be used, reproduced,
 * distributed, or sublicensed in any form without the express written consent of
 * the copyright owner, except as permitted by law.
 * Any unauthorized use, distribution, or modification of this source code,
 * or any portion thereof, may result in severe civil and criminal penalties,
 * and will be prosecuted to the maximum extent possible under the law.
 * For inquiries regarding usage or redistribution, please contact:
 *     TrueNine
 *     Email: <truenine304520@gmail.com>
 *     Website: [gitee.com/TrueNine]
 */
package net.yan100.compose.core.jackson

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import net.yan100.compose.core.lang.*
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneOffset

// FIXME 急需修复 date 的 转换消耗

class LocalDateTimeSerializer(private val zoneOffset: ZoneOffset) :
  JsonSerializer<LocalDateTime>() {
  override fun serialize(
    value: LocalDateTime,
    gen: JsonGenerator?,
    serializers: SerializerProvider?
  ) {
    gen?.writeNumber(value.toDate(zoneOffset).toLong())
  }
}

class LocalDateTimeDeserializer(private val zoneOffset: ZoneOffset) :
  JsonDeserializer<LocalDateTime>() {
  override fun deserialize(p: JsonParser?, ctxt: DeserializationContext?): LocalDateTime? {
    val timestamp: Long? = p?.valueAsString?.toLongOrNull()
    return timestamp?.toLocalDateTime(zoneOffset)
  }
}

class LocalDateSerializer(private val zoneOffset: ZoneOffset) : JsonSerializer<LocalDate>() {
  override fun serialize(value: LocalDate, gen: JsonGenerator?, serializers: SerializerProvider?) {
    gen?.writeNumber(value.toDate(zoneOffset).toLong())
  }
}

class LocalDateDeserializer(private val zoneOffset: ZoneOffset) : JsonDeserializer<LocalDate>() {
  override fun deserialize(p: JsonParser?, ctxt: DeserializationContext?): LocalDate? {
    val timestamp: Long? = p?.valueAsString?.toLongOrNull()
    return timestamp?.toLocalDate(zoneOffset)
  }
}

class LocalTimeSerializer(private val zoneOffset: ZoneOffset) : JsonSerializer<LocalTime>() {
  override fun serialize(value: LocalTime, gen: JsonGenerator?, serializers: SerializerProvider?) {
    gen?.writeNumber(value.toDate(zoneOffset).toLong())
  }
}

class LocalTimeDeserializer(private val zoneOffset: ZoneOffset) : JsonDeserializer<LocalTime>() {
  override fun deserialize(p: JsonParser?, ctxt: DeserializationContext?): LocalTime? {
    val timestamp: Long? = p?.valueAsString?.toLongOrNull()
    return timestamp?.toLocalTime(zoneOffset)
  }
}
