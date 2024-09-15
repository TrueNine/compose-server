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
package net.yan100.compose.rds.hibernate

import jakarta.annotation.Resource
import net.yan100.compose.rds.core.typing.AttachmentTyping
import net.yan100.compose.rds.entities.Attachment
import net.yan100.compose.rds.repositories.IAttachmentRepo
import org.springframework.boot.test.context.SpringBootTest
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
  @Resource
  lateinit var attRepo: IAttachmentRepo

  /** 保证在更新 null 后，可以设置为 null */
  @Test
  fun `test dynamic update annotation future`() {
    assertNotNull(attRepo)
    val firstInsertEntity =
      attRepo.save(
        Attachment().also {
          it.size = 133
          it.saveName = "233"
          it.attType = AttachmentTyping.ATTACHMENT
        }
      )

    assertNotNull(firstInsertEntity)
    val a = attRepo.findById(firstInsertEntity.id).get()
    println(firstInsertEntity)
    println(a)

    val save =
      attRepo.save(
        a.let {
          it.saveName = null
          it
        }
      )
    assertNull(save.saveName, "更新实体字段为 null 时，不能证券设置 null")
  }
}
