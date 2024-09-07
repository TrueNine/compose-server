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
package net.yan100.compose.core.encrypt

import org.apache.tomcat.util.http.fileupload.ByteArrayOutputStream
import java.io.ByteArrayInputStream
import java.io.IOException
import java.util.*
import java.util.zip.GZIPInputStream
import java.util.zip.GZIPOutputStream
import kotlin.test.Test

/**
 * @author TrueNine
 * @since 2024-01-20
 * @deprecated 不推荐，因为压缩达不到减小字符串效果
 */
class CompressTest {

  @Test
  fun `test compress gzip`() {
    val data =
      "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJULVNFUlZFUiIsImp0aSI6IkdyZWdvcmlhbi0xOTcwMDEwMSIsImVkdCI6IkJLRDhtUFlEaFZjZjlwRTZFd2JrL2NBS0RkMzgvUFJvakh0WG50UXlNTDc3SkNUVUIxR0tOSDI1YlBCL2wwUW43bVpIL1RSSnB3dWpWMXdlTzY1L1dOZSs3YmR3YWZRMUZlbm9JTlZQREJUMnlvREQ3MXN3djgvanYwNWhIK3ZyZ2x0QUJnejYxZkY0MW5vMFl2b3Y3SzFDZ3JpbkZUZ1hKRmlwSVBxTEVRVVFUSVgzK3d6WDNvM0NlWlZJSDAwdlR2WWRnckFBcGpBUERFK0NXS3R0MTNaR0xaNmdxTHRqeWxteUJXR1EwRGZQZ0p0VWZvczFVSUt0QVYrQnNPL1pQNW9zQU8yOWs3dDdWUE01UTF1Vk9EUmJYTjFvTGJMTkc4UldkZVBuTDFuOEN2WmNVY010enZmTENqaUtCdWR6dVhjMVFuZkhQOCtZc0d0b2twVDRaYUV5Tk1CR3dmY2ZJS0lYT2FWQVB4MHU0b1dTYWNnS2ZDTWJhSGdVb3d6UEJ3K0JCbHVnaXZrcThSdjNZWUgyNWY1RExiTlRXUWZxZmVURUFTMHI0Z0U0QzhTT1JsU0tiMmxnMjh3TTFCbkJSTVRsRHF3T3duUk9USUhoWk1sV3hUYXlhRStmVGpYTyIsImV4cCI6MTcwNTc4NjM4Mn0.axCZH6iYRwZKhlMjjj7eehD75Z3gSZakOj5O-X83Oj-eW8vL5B6oDiS3GBt99LVQe4hFuxwkkfjKjX83cQdaCgNtmWlThlZ3x-eGeSi9ghBY5_8-fLRDwKSvwqXvlGdeXB--fPvmgqjW_u-RCklmAzQxhu2-JwqsJRCHKX-9mhs"
    val enc = compress(data)
    println(enc)
    println(decompress(enc))
  }

  @Throws(IOException::class)
  fun compress(str: String?): String? {
    if (str.isNullOrEmpty()) {
      return str
    }
    val baos = ByteArrayOutputStream()
    GZIPOutputStream(baos).use { gzip -> gzip.write(str.toByteArray(Charsets.ISO_8859_1)) }
    return Base64.getEncoder().encodeToString(baos.toByteArray())
  }

  @Throws(IOException::class)
  fun decompress(compressedStr: String?): String? {
    if (compressedStr.isNullOrEmpty()) {
      return compressedStr
    }
    val decodedBytes = Base64.getDecoder().decode(compressedStr)
    val baos = ByteArrayOutputStream()
    GZIPInputStream(ByteArrayInputStream(decodedBytes)).use { gis ->
      val buffer = ByteArray(1024)
      var len: Int
      while ((gis.read(buffer).also { len = it }) != -1) {
        baos.write(buffer, 0, len)
      }
    }
    return String(baos.toByteArray(), Charsets.ISO_8859_1)
  }
}
