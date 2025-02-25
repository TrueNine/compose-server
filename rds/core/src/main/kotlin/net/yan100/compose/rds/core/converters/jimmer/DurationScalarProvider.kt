package net.yan100.compose.rds.core.converters.jimmer

import java.time.Duration
import org.babyfish.jimmer.sql.runtime.ScalarProvider

class DurationScalarProvider : ScalarProvider<Duration?, String?> {
  override fun toScalar(sqlValue: String): Duration? {
    return Duration.parse(sqlValue)
  }

  override fun toSql(scalarValue: Duration): String {
    return scalarValue.toString()
  }
}
