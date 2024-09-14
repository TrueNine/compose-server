/*
 *  Copyright (c) 2020-2024 TrueNine. All rights reserved.
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
 *     email: <truenine304520@gmail.com>
 *     website: <github.com/TrueNine>
 */
package net.yan100.compose.depend.jackson

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonToken
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer
import net.yan100.compose.core.*
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneOffset


class LocalDateTimeSerializerZ(private val zoneOffset: ZoneOffset) : AbstractTypedSerializer<LocalDateTime>(JsonToken.VALUE_NUMBER_INT) {
  override fun serialize(
    value: LocalDateTime,
    gen: JsonGenerator?,
    serializers: SerializerProvider?
  ) {
    val v = value.toDate(zoneOffset).toLong()
    gen?.writeNumber(v)
  }
}

class LocalDateTimeDeserializerZ(private val zoneOffset: ZoneOffset) : LocalDateTimeDeserializer() {
  override fun deserialize(p: JsonParser?, ctxt: DeserializationContext?): LocalDateTime? {
    val timestamp: Long? = p?.valueAsString?.toLongOrNull()
    val v = timestamp?.toLocalDateTime(zoneOffset)
    return v
  }
}

class LocalDateSerializerX(private val zoneOffset: ZoneOffset) : AbstractTypedSerializer<LocalDate>(JsonToken.VALUE_NUMBER_INT) {
  override fun serialize(
    value: LocalDate,
    gen: JsonGenerator?,
    serializers: SerializerProvider?
  ) {
    gen?.writeNumber(value.toDate(zoneOffset).toLong())
  }
}

class LocalDateDeserializerX(private val zoneOffset: ZoneOffset) : JsonDeserializer<LocalDate>() {
  override fun deserialize(p: JsonParser?, ctxt: DeserializationContext?): LocalDate? {
    val timestamp: Long? = p?.valueAsString?.toLongOrNull()
    val v = timestamp?.toLocalDate(zoneOffset)
    return v
  }
}

class LocalTimeSerializerY(private val zoneOffset: ZoneOffset) :
  AbstractTypedSerializer<LocalTime>(JsonToken.VALUE_NUMBER_INT) {

  override fun serialize(
    value: LocalTime,
    gen: JsonGenerator?,
    serializers: SerializerProvider?
  ) {
    gen?.writeNumber(value.toDate(zoneOffset).toLong())
  }
}

class LocalTimeDeserializerY(private val zoneOffset: ZoneOffset) : LocalTimeDeserializer() {
  override fun deserialize(p: JsonParser?, ctxt: DeserializationContext?): LocalTime? {
    val timestamp: Long? = p?.valueAsString?.toLongOrNull()
    val v = timestamp?.toLocalTime(zoneOffset)
    return v
  }
}
