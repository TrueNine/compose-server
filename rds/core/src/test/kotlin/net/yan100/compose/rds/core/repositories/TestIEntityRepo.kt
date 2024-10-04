package net.yan100.compose.rds.core.repositories

import net.yan100.compose.rds.core.IRepo
import net.yan100.compose.rds.core.entities.TestIEntity
import org.springframework.stereotype.Repository

@Repository
interface TestIEntityRepo : IRepo<TestIEntity> {
}
