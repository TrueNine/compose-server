package com.truenine.component.rds.base

import com.truenine.component.core.lang.LogKt
import com.truenine.component.rds.RdsEntrance
import com.truenine.component.rds.dao.DbTestPresortTreeDao
import com.truenine.component.rds.repo.DbTestPresortTreeRepo
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.annotation.Rollback
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.support.TransactionTemplate
import org.testng.annotations.Test



@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@SpringBootTest(classes = [RdsEntrance::class])
open class PresortTreeRepoTest : AbstractTestNGSpringContextTests() {
  private val log = LogKt.getLog(this::class)

  @Autowired
  lateinit var treeRepo: DbTestPresortTreeRepo

  @Autowired
  lateinit var tt: TransactionTemplate


  @Test
  open fun testSaveChild() {
    val root = DbTestPresortTreeDao()
    val a = DbTestPresortTreeDao()
    val savedRoot = treeRepo.saveChild(null, root)
    treeRepo.saveChild(savedRoot, a)
    log.debug("savedRoot = {}", savedRoot)
    treeRepo.deleteAll()
  }
}
