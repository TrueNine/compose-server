package net.yan100.compose.rds.entities

import jakarta.persistence.Entity
import jakarta.persistence.Table
import net.yan100.compose.rds.core.entities.IEntity
import java.time.Duration


@Entity
@Table(name = "db_test_duration_converter")
data class DbTestDurationConverterEntity(
    var durations: Duration?
) : IEntity() {
    constructor() : this(null)
}
