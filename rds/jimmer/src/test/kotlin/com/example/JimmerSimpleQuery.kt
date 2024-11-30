package com.example

import com.example.entities.Book
import com.example.entities.edition
import com.example.entities.name
import com.example.entities.store
import jakarta.annotation.Resource
import net.yan100.compose.core.Pq
import net.yan100.compose.rds.jimmer.desc
import net.yan100.compose.rds.jimmer.`desc?`
import net.yan100.compose.rds.jimmer.fetchPage
import net.yan100.compose.rds.jimmer.toIPage
import net.yan100.compose.testtookit.log
import org.babyfish.jimmer.sql.kt.KSqlClient
import org.babyfish.jimmer.sql.kt.ast.expression.`like?`
import org.springframework.boot.test.context.SpringBootTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertNotNull

@SpringBootTest
class JimmerSimpleQuery {
  lateinit var client: KSqlClient @Resource set

  @BeforeTest
  fun setup() {
    assertNotNull(client)
  }

  @Test
  fun `ensure simple query worked`() {
    val bName = "Learning GraphQL"
    val query = client.createQuery(Book::class) {

      table.store.name
      where += table.name `like?` bName
      orderBy(table.edition desc true)
      orderBy(table.name `desc?` null)
      select(table)
    }.fetchPage(Pq[0, 42]).toIPage()

    query.d.forEach {
      log.info("created: {}", it)
    }
  }
}
