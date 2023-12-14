package net.yan100.compose.rds.base

import net.yan100.compose.core.lang.slf4j
import net.yan100.compose.rds.RdsEntrance
import net.yan100.compose.rds.entities.DbTestTreeEntity
import net.yan100.compose.rds.repositories.DbTestTreeRepo
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.Rollback
import org.springframework.transaction.support.TransactionTemplate
import kotlin.test.assertContains
import kotlin.test.assertTrue

@Rollback
@SpringBootTest(classes = [RdsEntrance::class])
class TreeRepositoryTest {
  private val log = slf4j(this::class)

  @Autowired
  lateinit var treeRepo: DbTestTreeRepo

  @Autowired
  lateinit var tt: TransactionTemplate

  @Test
  @Rollback
  fun testSaveChild() {
    val b = DbTestTreeEntity()
    val c = DbTestTreeEntity()
    val d = DbTestTreeEntity()
    val savedRoot = treeRepo.saveChild(child =  DbTestTreeEntity())
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
