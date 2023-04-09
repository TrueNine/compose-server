package com.truenine.component.rds.base

import com.truenine.component.core.lang.LogKt
import com.truenine.component.rds.RdsEntrance
import com.truenine.component.rds.entity.DbTestPresortTreeEntity
import com.truenine.component.rds.repo.DbTestPresortTreeRepo
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.annotation.Rollback
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.support.TransactionTemplate
import org.testng.annotations.Test
import kotlin.test.assertContains
import kotlin.test.assertTrue

@Rollback
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@SpringBootTest(classes = [RdsEntrance::class])
open class PresortTreeRepoTest : AbstractTestNGSpringContextTests() {
  private val log = LogKt.getLog(this::class)

  @Autowired
  lateinit var treeRepo: DbTestPresortTreeRepo

  @Autowired
  lateinit var tt: TransactionTemplate

  @Test
  @Transactional
  open fun testSaveChild() {
    val b = DbTestPresortTreeEntity()
    val c = DbTestPresortTreeEntity()
    val d = DbTestPresortTreeEntity()
    val savedRoot = treeRepo.saveChild(null, DbTestPresortTreeEntity())
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
