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
package net.yan100.compose.rds.base

import kotlin.test.assertContains
import kotlin.test.assertTrue
import net.yan100.compose.core.lang.slf4j
import net.yan100.compose.rds.RdsEntrance
import net.yan100.compose.rds.entities.DbTestTreeEntity
import net.yan100.compose.rds.repositories.DbTestTreeRepo
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.Rollback
import org.springframework.transaction.support.TransactionTemplate

@Rollback
@SpringBootTest(classes = [RdsEntrance::class])
class TreeRepositoryTest {
  private val log = slf4j(this::class)

  @Autowired lateinit var treeRepo: DbTestTreeRepo

  @Autowired lateinit var tt: TransactionTemplate

  @Test
  @Rollback
  fun testSaveChild() {
    val b = DbTestTreeEntity()
    val c = DbTestTreeEntity()
    val d = DbTestTreeEntity()
    val savedRoot = treeRepo.saveChild(child = DbTestTreeEntity())
    val savedChildren = treeRepo.saveChildren(savedRoot) { listOf(b, c, d) }

    val nodeIndexes = savedChildren.map { listOf(it.rln, it.rrn) }.flatten()

    // 节点的值必须固定
    assertTrue("保存的根节点索引不对") { savedRoot.rln == 1L && savedRoot.rrn == 2L }

    // 添加后集合内不可有重复数据
    assertTrue("添加后出现重复数据") { nodeIndexes.distinct().size == nodeIndexes.size }

    // 集合内必须包含固定的值
    listOf(2, 3, 4, 5, 6, 7)
      .map { it.toLong() }
      .forEach { assertContains(nodeIndexes, it, "没有包含固定的值") }
  }
}
