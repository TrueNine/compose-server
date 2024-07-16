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
package net.yan100.compose.core.jackson

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.*
import com.fasterxml.jackson.databind.ser.ContextualSerializer
import net.yan100.compose.core.annotations.BigIntegerAsString
import net.yan100.compose.core.extensionfunctions.hasText

class StringAsLongDeserializer : JsonDeserializer<Long?>(), ContextualSerializer {
  override fun deserialize(p: JsonParser?, ctxt: DeserializationContext?): Long? {
    return p?.let { parser ->
      if (parser.text.hasText()) {
        parser.text.toLongOrNull()
      } else null
    }
  }

  override fun createContextual(prov: SerializerProvider?, property: BeanProperty?): JsonSerializer<*>? {
    return property?.let { p ->
      val ref: BigIntegerAsString? = p.getAnnotation(BigIntegerAsString::class.java)
      if (null != ref && p.type.rawClass == Long::class.java) LongAsStringSerializer() else prov?.findValueSerializer(property.type, property)
    } ?: prov?.findNullValueSerializer(property)
  }
}
