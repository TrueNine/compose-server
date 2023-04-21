package com.truenine.component.rds.base

import com.truenine.component.core.lang.slf4j
import com.truenine.component.rds.RdsEntrance
import com.truenine.component.rds.entity.DbTestTreeEntity
import com.truenine.component.rds.repository.DbTestTreeRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.Rollback
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests
import org.springframework.transaction.support.TransactionTemplate
import org.testng.annotations.Test
import kotlin.test.assertContains
import kotlin.test.assertTrue

@Rollback
@SpringBootTest(classes = [RdsEntrance::class])
class TreeRepositoryTest : AbstractTestNGSpringContextTests() {
  private val log = slf4j(this::class)

  @Autowired
  lateinit var treeRepo: DbTestTreeRepository

  @Autowired
  lateinit var tt: TransactionTemplate

  @Test
  @Rollback
  fun testSaveChild() {
    val b = DbTestTreeEntity()
    val c = DbTestTreeEntity()
    val d = DbTestTreeEntity()
    val savedRoot = treeRepo.saveChild(null, DbTestTreeEntity())
    val savedChildren = treeRepo.saveChildren(savedRoot) { listOf(b, c, d) }

    val nodeIndexes = savedChildren.map {
      listOf(it.rln, it.rrn)
    }.flatten()

    // 节点的值必须固定
    assertTrue("保存的根节点索引不对") {
      savedRoot.rln == 1L
        && savedRoot.rrn == 2L
    }

    // 添加后集合内不可有重复数据
    assertTrue("添加后出现重复数据") {
      nodeIndexes.distinct().size == nodeIndexes.size
    }

    // 集合内必须包含固定的值
    listOf(2, 3, 4, 5, 6, 7).map { it.toLong() }
      .forEach {
        assertContains(nodeIndexes, it, "没有包含固定的值")
      }
  }
}
