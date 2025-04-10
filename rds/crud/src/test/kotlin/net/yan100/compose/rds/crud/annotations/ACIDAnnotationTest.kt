package net.yan100.compose.rds.crud.annotations

import jakarta.annotation.Resource
import net.yan100.compose.rds.crud.repositories.jpa.IUserAccountRepo
import net.yan100.compose.testtookit.RDBRollback
import org.junit.jupiter.api.assertThrows
import org.springframework.boot.test.context.SpringBootTest
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@RDBRollback
@SpringBootTest
class ACIDAnnotationTest {
  lateinit var usrRepo: IUserAccountRepo @Resource set

  lateinit var launchBean: LaunchBean @Resource set

  @BeforeTest
  fun setup() {
    assertNotNull(launchBean, "确保测试类能够被注入")
    assertNotNull(usrRepo, "确保用户仓库能够被注入")
  }

  @Test
  @RDBRollback
  fun `事务回滚后确保数据一致性`() {
    // 记录初始数据状态
    val initialData = usrRepo.findAll()
    val initialSize = initialData.size

    // 执行会抛出异常的事务操作
    assertThrows<IllegalStateException> { launchBean.throwTransactionalSave() }

    // 验证数据是否回滚
    val finalData = usrRepo.findAll()
    assertEquals(initialSize, finalData.size, "事务回滚后数据量应该保持不变")
    assertTrue(initialData.containsAll(finalData), "事务回滚后数据内容应该保持一致")
  }

  @Test
  fun `非事务操作不会回滚`() {
    val initialData = usrRepo.findAll()
    val initialSize = initialData.size

    assertThrows<IllegalStateException> { launchBean.throwSaveAcid() }

    val finalData = usrRepo.findAll()
    assertEquals(initialSize, finalData.size, "非事务操作后数据量应该保持不变")
  }

  @Test
  fun `事务操作成功时数据应该被保存`() {
    val initialData = usrRepo.findAll()
    val initialSize = initialData.size

    launchBean.saveOk()

    val finalData = usrRepo.findAll()
    assertEquals(initialSize + 1, finalData.size, "事务操作成功后数据量应该增加")
  }

  @Test
  fun `并发事务操作应该保持隔离性`() {
    val initialData = usrRepo.findAll()
    val initialSize = initialData.size

    // 使用 CountDownLatch 来同步线程
    val latch = CountDownLatch(2)
    var exceptionThrown = false

    // 模拟并发操作
    val thread1 = Thread {
      try {
        launchBean.throwTransactionalSave()
      } catch (_: Exception) {
        exceptionThrown = true
      } finally {
        latch.countDown()
      }
    }

    val thread2 = Thread {
      try {
        launchBean.saveOk()
      } finally {
        latch.countDown()
      }
    }

    thread1.start()
    thread2.start()

    // 等待两个线程都完成
    latch.await(5, TimeUnit.SECONDS)

    // 验证结果
    assertTrue(exceptionThrown, "应该抛出异常")
    val finalData = usrRepo.findAll()
    assertEquals(initialSize + 1, finalData.size, "并发事务操作后数据应该只增加成功的事务操作")

    // 验证数据一致性
    val newData = finalData.filter { !initialData.contains(it) }
    assertEquals(1, newData.size, "应该只新增一条数据")
  }
}
