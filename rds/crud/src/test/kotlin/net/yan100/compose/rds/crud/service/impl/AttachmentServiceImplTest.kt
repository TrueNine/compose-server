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
package net.yan100.compose.rds.crud.service.impl

import jakarta.annotation.Resource
import net.yan100.compose.core.Pq
import net.yan100.compose.core.generator.ISnowflakeGenerator
import net.yan100.compose.rds.core.typing.AttachmentTyping
import net.yan100.compose.rds.crud.entities.jpa.Attachment
import net.yan100.compose.security.crypto.Keys
import net.yan100.compose.testtookit.RDBRollback
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import kotlin.test.*

@RDBRollback
@SpringBootTest
class AttachmentServiceImplTest {
  lateinit var attachmentService: AttachmentServiceImpl @Resource set
  lateinit var snowflake: ISnowflakeGenerator @Resource set

  fun getAtt(att: (Attachment) -> Attachment): Attachment {
    return attachmentService.post(Attachment().run(att))
  }

  @Test
  @RDBRollback
  fun testExistsByBaseUrl_Exists() {
    val baseUrl = "http://example.com"
    getAtt {
      it.baseUrl = baseUrl
      it.attType = AttachmentTyping.BASE_URL
      it
    }
    val result = attachmentService.existsByBaseUrl(baseUrl)
    assertTrue { result }
  }

  @Test
  @RDBRollback
  fun testExistsByBaseUrl_NotExists() {
    val baseUrl = "http://notexists.com"
    val result = attachmentService.existsByBaseUrl(baseUrl)
    assertFalse { result }
  }

  @Test
  @RDBRollback
  fun testFindByBaseUrl_Exists() {
    val baseUrl = "http://example.com"
    getAtt {
      it.attType = AttachmentTyping.BASE_URL
      it.baseUrl = baseUrl
      it
    }
    val result = attachmentService.fetchByBaseUrl(baseUrl)
    assertNotNull(result)
  }

  @Test
  @RDBRollback
  fun testFindByBaseUrl_NotExists() {
    val baseUrl = "http://notexists.com"
    val result = attachmentService.fetchByBaseUrl(baseUrl)
    assertNull(result)
  }

  @Test
  @RDBRollback
  fun testFindFullUrlById_Exists() {
    val b = getAtt {
      it.baseUrl = "https://www.baidu.com"
      it.attType = AttachmentTyping.BASE_URL
      it
    }
    val e = getAtt {
      it.urlId = b.id
      it.metaName = "ab"
      it.attType = AttachmentTyping.ATTACHMENT
      it
    }
    val all = attachmentService.fetchAll()
    println(all)
    val result = attachmentService.fetchFullUrlById(e.id)
    assertNotNull(result)
  }

  @Test
  @RDBRollback
  fun testFindFullUrlById_NotExists() {
    val id = snowflake.nextString()
    val result = attachmentService.fetchFullUrlById(id)
    assertNull(result)
  }

  @Test
  @RDBRollback
  fun testFindAllFullUrlByMetaNameStartingWith() {
    val metaName = "test"
    val page = Pq[10, 1, false]
    val result = attachmentService.fetchAllFullUrlByMetaNameStartingWith(metaName, page)
    assertNotNull(result)
    assertEquals(0, result.t)
  }

  @Test
  @RDBRollback
  fun testExistsByBaseUrl_EmptyBaseUrl() {
    val baseUrl = ""
    val result = attachmentService.existsByBaseUrl(baseUrl)
    assertFalse(result)
  }

  @Test
  @RDBRollback
  fun testFindByBaseUrl_EmptyBaseUrl() {
    val baseUrl = Keys.generateRandomAsciiString(32)
    val result = attachmentService.fetchByBaseUrl(baseUrl)
    assertNull(result)
  }

  @Test
  @RDBRollback
  fun testFindFullUrlById_NegativeId() {
    val id = snowflake.nextString()
    val result = attachmentService.fetchFullUrlById(id)
    assertNull(result)
  }

  @Test
  @RDBRollback
  fun testFindAllFullUrlByMetaNameStartingWith_NegativePage() {
    val metaName = "test"
    val page = Pq[-1, 10, false]
    assertFails { attachmentService.fetchAllFullUrlByMetaNameStartingWith(metaName, page) }
  }
}
