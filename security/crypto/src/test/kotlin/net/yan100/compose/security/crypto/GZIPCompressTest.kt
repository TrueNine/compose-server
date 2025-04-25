package net.yan100.compose.security.crypto

import net.yan100.compose.testtoolkit.log
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
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
class GZIPCompressTest {

  @Test
  fun `compress gzip`() {
    val data =
      "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJULVNFUlZFUiIsImp0aSI6IkdyZWdvcmlhbi0xOTcwMDEwMSIsImVkdCI6IkJLRDhtUFlEaFZjZjlwRTZFd2JrL2NBS0RkMzgvUFJvakh0WG50UXlNTDc3SkNUVUIxR0tOSDI1YlBCL2wwUW43bVpIL1RSSnB3dWpWMXdlTzY1L1dOZSs3YmR3YWZRMUZlbm9JTlZQREJUMnlvREQ3MXN3djgvanYwNWhIK3ZyZ2x0QUJnejYxZkY0MW5vMFl2b3Y3SzFDZ3JpbkZUZ1hKRmlwSVBxTEVRVVFUSVgzK3d6WDNvM0NlWlZJSDAwdlR2WWRnckFBcGpBUERFK0NXS3R0MTNaR0xaNmdxTHRqeWxteUJXR1EwRGZQZ0p0VWZvczFVSUt0QVYrQnNPL1pQNW9zQU8yOWs3dDdWUE01UTF1Vk9EUmJYTjFvTGJMTkc4UldkZVBuTDFuOEN2WmNVY010enZmTENqaUtCdWR6dVhjMVFuZkhQOCtZc0d0b2twVDRaYUV5Tk1CR3dmY2ZJS0lYT2FWQVB4MHU0b1dTYWNnS2ZDTWJhSGdVb3d6UEJ3K0JCbHVnaXZrcThSdjNZWUgyNWY1RExiTlRXUWZxZmVURUFTMHI0Z0U0QzhTT1JsU0tiMmxnMjh3TTFCbkJSTVRsRHF3T3duUk9USUhoWk1sV3hUYXlhRStmVGpYTyIsImV4cCI6MTcwNTc4NjM4Mn0.axCZH6iYRwZKhlMjjj7eehD75Z3gSZakOj5O-X83Oj-eW8vL5B6oDiS3GBt99LVQe4hFuxwkkfjKjX83cQdaCgNtmWlThlZ3x-eGeSi9ghBY5_8-fLRDwKSvwqXvlGdeXB--fPvmgqjW_u-RCklmAzQxhu2-JwqsJRCHKX-9mhs"
    val enc = compress(data)
    log.info(enc)
    log.info(decompress(enc))
  }

  @Throws(IOException::class)
  fun compress(str: String?): String? {
    if (str.isNullOrBlank()) {
      return str
    }
    val baos = ByteArrayOutputStream()
    GZIPOutputStream(baos).use { gzip ->
      gzip.write(str.toByteArray(Charsets.ISO_8859_1))
    }
    return baos.toByteArray().encodeBase64String
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
