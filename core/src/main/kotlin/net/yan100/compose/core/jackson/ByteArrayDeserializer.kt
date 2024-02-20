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

import com.fasterxml.jackson.core.JsonParseException
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonNode

// FIXME 这种序列化效率太低
class ByteArrayDeserializer : JsonDeserializer<ByteArray?>() {
  override fun deserialize(p: JsonParser?, ctxt: DeserializationContext?): ByteArray? {
    return p?.let {
      val str = it.readValueAsTree<JsonNode>()
      val byteArray = ByteArray(str.size())
      for (i in 0 until str.size()) {
        val j = str.get(i)
        if (j.isNumber) byteArray[i] = j.asInt().toByte()
        else throw JsonParseException("序列化为 byte 错误")
      }
      byteArray
    }
  }
}
