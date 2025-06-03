package net.yan100.compose.rds.converters

import org.babyfish.jimmer.sql.runtime.ScalarProvider
import java.time.Period

class PeriodScalarProvider : ScalarProvider<Period?, String?> {
  override fun toScalar(sqlValue: String): Period? {
    return Period.parse(sqlValue)
  }

  override fun toSql(scalarValue: Period): String {
    return scalarValue.toString()
  }
}
