package net.yan100.compose.rds.crud.service.impl

import net.yan100.compose.core.slf4j
import net.yan100.compose.rds.core.ICrud
import net.yan100.compose.rds.core.jpa
import net.yan100.compose.rds.crud.entities.jpa.FlywaySchemaHistory
import net.yan100.compose.rds.crud.repositories.jpa.IFlywaySchemaHistoryRepo
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties
import org.springframework.stereotype.Service

private enum class DbType {
  MYSQL,
  MYSQLJ,
  POSTGRESQL,
  ORACLE,
  SQLSERVER,
  SQLITE,
  H2,
  HSQLDB,
  DB2,
}

private val log = slf4j(FlywaySchemaHistoryServiceImpl::class)

@Service
class FlywaySchemaHistoryServiceImpl(
  private val fRepo: IFlywaySchemaHistoryRepo,
  private val dbProperty: DataSourceProperties,
) :
  net.yan100.compose.rds.crud.service.IFlywaySchemaHistoryService,
  ICrud<FlywaySchemaHistory> by jpa(fRepo) {

  private fun getDbType(): DbType? {
    val className = dbProperty.driverClassName
    return when (className) {
      "com.mysql.cj.jdbc.Driver" -> DbType.MYSQLJ
      "com.mysql.jdbc.Driver" -> DbType.MYSQL
      "org.postgresql.Driver" -> DbType.POSTGRESQL
      "oracle.jdbc.driver.OracleDriver" -> DbType.ORACLE
      "com.microsoft.sqlserver.jdbc.SQLServerDriver" -> DbType.SQLSERVER
      "org.hsqldb.jdbcDriver" -> DbType.HSQLDB
      "org.h2.Driver" -> DbType.H2
      "com.ibm.db2.jcc.DB2Driver" -> DbType.DB2
      else -> null
    }
  }

  override fun clean() {
    when (getDbType()) {
      DbType.MYSQLJ -> fRepo.nativeDropTableForMysql()
      DbType.MYSQL -> fRepo.nativeDropTableForMysql()
      DbType.POSTGRESQL -> fRepo.nativeDropTableForPostgresql()
      else -> log.warn("不支持的数据库类型: {}", dbProperty.driverClassName)
    }
  }
}
