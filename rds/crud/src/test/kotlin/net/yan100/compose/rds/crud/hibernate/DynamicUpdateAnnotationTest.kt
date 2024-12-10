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
package net.yan100.compose.rds.crud.hibernate

import jakarta.annotation.Resource
import net.yan100.compose.rds.core.typing.AttachmentTyping
import net.yan100.compose.rds.crud.entities.jpa.Attachment
import net.yan100.compose.rds.crud.repositories.jpa.IAttachmentRepo
import net.yan100.compose.testtookit.RDBRollback
import net.yan100.compose.testtookit.log
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.repository.findByIdOrNull
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertNull

/**
 * ## hibernate 测试
 *
 * 该测试针对 hibernate 的一些特性进行准确性测试。 例如 某些不明确的方法或者特性，须将其以错误形式明确
 *
 * @author TrueNine
 * @since 2024-02-30
 */
@SpringBootTest
class DynamicUpdateAnnotationTest {
  lateinit var attRepo: IAttachmentRepo @Resource set

  @BeforeTest
  fun setup() {
    assertNotNull(attRepo)
  }

  /** 保证在更新 null 后，可以设置为 null */
  @Test
  @RDBRollback
  fun `test dynamic update annotation future`() {
    val att = Attachment(
      size = 133,
      saveName = "233",
      attType = AttachmentTyping.ATTACHMENT
    )
    val firstInsertEntity = attRepo.save(
      att
    )

    assertNotNull(firstInsertEntity)

    val a = attRepo.findByIdOrNull(firstInsertEntity.id)
    assertNotNull(a)
    log.info("firstInsertEntity: {}", firstInsertEntity)
    log.info("a: {}", a)

    val save =
      attRepo.save(
        a.apply {
          saveName = null
          attType = AttachmentTyping.BASE_URL
        }
      )
    assertNull(save.saveName, "更新实体字段为 null 时，不能正确设置 null")
  }
}
